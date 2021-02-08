package no.nsd.qddt.domain

import no.nsd.qddt.domain.classes.interfaces.Version as EmbeddedVersion
import no.nsd.qddt.domain.classes.elementref.ElementKind
import no.nsd.qddt.domain.classes.exception.StackTraceFilter
import no.nsd.qddt.domain.classes.interfaces.IArchived
import no.nsd.qddt.domain.classes.interfaces.IDomainObject
import no.nsd.qddt.domain.classes.pdf.PdfReport
import no.nsd.qddt.domain.agency.Agency
import no.nsd.qddt.domain.user.User
import no.nsd.qddt.utils.StringTool.IsNullOrTrimEmpty

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

import org.springframework.security.core.context.SecurityContextHolder
import org.hibernate.envers.Audited
import org.hibernate.envers.NotAudited
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.stream.Collectors
import javax.persistence.*

/**
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
@Audited
@MappedSuperclass
abstract class AbstractEntityAudit(

    @JsonBackReference(value = "agentRef")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agency_id")
    override var agency : Agency? = null,

    @Column(updatable = false)
    val basedOnObject: UUID? = null,

    @Column( updatable = false)
    val basedOnRevision: Int? = null,

    @Embedded
    override var version: EmbeddedVersion= EmbeddedVersion()

    var xmlLang: String? = "en-GB"

) : AbstractEntity(), IDomainObject {
    /**
     * ChangeKinds are the different ways an entity can be modified by the system/user.
     * First entry will always be CREATED.
     * TYPO, can be used modify without breaking a release.
     * Every other version is a IN_DEVELOPMENT change.
     */
    enum class ChangeKind(val label: String, val description: String) {
        CREATED("Created", "New element status"),
         BASED_ON("Based on", "Based on copy"),
         NEW_COPY("New Copy","Copy new"),
         TRANSLATED("Translated", "Translation of source"),
         REFERENCED("Reference of","Concepts can be copied as a reference, to facilitate hierarchical revision trees"),
         UPDATED_PARENT("Parent Updated", "ChildSaved as part of parent save"),
         UPDATED_CHILD("Child Updated","ParentSaved as part of child save"),
         UPDATED_HIERARCHY_RELATION("Hierarchy Relation Updated",
            "Element added to a collection, no changes to element itself"),
         IN_DEVELOPMENT("In Development", "UnfinishedWork"),
         TYPO("NoMeaningChange","Typo or No Meaning Change"),

         CONCEPTUAL("ConceptualImprovement", "Conceptual Improvement"),
         EXTERNAL("RealLifeChange","Real Life Change"),

         OTHER("OtherPurpose", "Other Purpose"),
         ADDED_CONTENT("AddContentElement","Added content later on"),

         ARCHIVED("Archived", "READ ONLY"),
         TO_BE_DELETED("ToBeDeleted",
            "This has been marked for deletion, but we need to see it a tiny bit longer.");

        override fun toString(): String {
            return """{ "ChangeKind": ${"\"" + label + "\""}}"""
        }
    }

    /**
     * I am the beginning of the end, and the end of time and space.
     * I am essential to creation, and I surround every place.
     * What am I?
     */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var changeKind: ChangeKind = ChangeKind.CREATED
        set(value) {
            if (field == ChangeKind.IN_DEVELOPMENT &&
                (value == ChangeKind.UPDATED_HIERARCHY_RELATION ||
                 value == ChangeKind.UPDATED_PARENT ||
                 value == ChangeKind.UPDATED_CHILD)
            ) {
                //BUGFIX https://github.com/DASISH/qddt-client/issues/546
                return
            }
            field = value
        }

    @Column(name = "change_comment", nullable = false)
    var changeComment: String? = ChangeKind.CREATED.description

    @NotAudited
    @OrderColumn(name = "owner_idx")
    @OneToMany(mappedBy = "ownerId", cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER, orphanRemoval = true)
    var comments: List<Comment> = ArrayList()

    @JsonSerialize
    @JsonDeserialize
    @Transient
    override var classKind: String = 
        try { ElementKind.getEnum(this.javaClass.simpleName).toString() } 
        catch (e: Exception) {this.javaClass.simpleName}


// TODO : moce these to PrePersist class

    @PrePersist
    private fun onInsert() {
        val user = SecurityContextHolder.getContext().authentication.details as User
        agency = user.agency
        when (this.xmlLang) {
            null -> xmlLang = user.agency.xmlLang
        }
        beforeInsert()
    }

    @PreUpdate
    private fun onUpdate() {
        try {
            var ver: EmbeddedVersion? = version
            var change = changeKind

            // it is illegal to update an entity with "Creator statuses" (CREATED...BASEDON)
            if ( (change.ordinal <= ChangeKind.REFERENCED.ordinal)  and !ver!!.isModified) {
                change = ChangeKind.IN_DEVELOPMENT
                changeKind = change
            }
            if (IsNullOrTrimEmpty(changeComment)) // insert default comment if none was supplied, (can occur with auto touching (hierarchy updates etc))
                changeComment = change.description
            when (change) {
                ChangeKind.CREATED
                    -> if (changeComment == null) changeComment = change.description
                ChangeKind.BASED_ON, ChangeKind.NEW_COPY, ChangeKind.TRANSLATED
                    -> ver = EmbeddedVersion()
                ChangeKind.REFERENCED, ChangeKind.TO_BE_DELETED
                    -> {}
                ChangeKind.UPDATED_PARENT, ChangeKind.UPDATED_CHILD, ChangeKind.UPDATED_HIERARCHY_RELATION
                    -> ver.versionLabel = ""
                ChangeKind.IN_DEVELOPMENT -> ver.versionLabel = ChangeKind.IN_DEVELOPMENT.name
                ChangeKind.TYPO -> {
                    ver.minor++
                    ver.versionLabel = ""
                }
                ChangeKind.CONCEPTUAL, ChangeKind.EXTERNAL, ChangeKind.OTHER, ChangeKind.ADDED_CONTENT -> {
                    ver.major++
                    ver.versionLabel =""
                }
                ChangeKind.ARCHIVED -> {
                    (this as IArchived).isArchived =true
                    ver.versionLabel =""
                }
            }
            version = ver
            beforeUpdate()
        } catch (ex: Exception) {
            LOG.error("AbstractEntityAudit::onUpdate", ex)
        }
    }


    val isBasedOn get() = changeKind == ChangeKind.BASED_ON || changeKind == ChangeKind.NEW_COPY || changeKind == ChangeKind.TRANSLATED || changeKind == ChangeKind.REFERENCED

    val isNewCopy get() = (changeKind == ChangeKind.NEW_COPY || id == null && changeKind != ChangeKind.CREATED)

    fun makePdf(): ByteArrayOutputStream {
        val pdfOutputStream = ByteArrayOutputStream()
        try {
            PdfReport(pdfOutputStream).use { pdf ->
                fillDoc(pdf, "")
                pdf.createToc()
            }
        } catch (ex: Exception) {
            with(LOG) {
                error("makePDF", ex)
                debug(
                    StackTraceFilter.filter(ex.stackTrace).stream().map { it?.methodName }
                    .collect(Collectors.joining(","))
                )
            }
        }
        return pdfOutputStream
    }

    abstract fun fillDoc(pdfReport: PdfReport, counter: String)

    protected abstract fun beforeUpdate() 
    protected abstract fun beforeInsert()

}
