package no.nsd.qddt.model.classes

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.classes.elementref.ElementKind
import no.nsd.qddt.model.exception.StackTraceFilter
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.Comment
import no.nsd.qddt.model.Agency
import no.nsd.qddt.model.User
import no.nsd.qddt.utils.StringTool.IsNullOrTrimEmpty
import org.hibernate.envers.Audited
import org.hibernate.envers.NotAudited
import org.hibernate.envers.RelationTargetAuditMode
import org.springframework.security.core.context.SecurityContextHolder
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.util.*
import java.util.stream.Collectors
import javax.persistence.*
import no.nsd.qddt.model.classes.Version as EmbeddedVersion

/**
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
@Audited
@MappedSuperclass
abstract class AbstractEntityAudit(

    @Column(name="based_on_object",updatable = false)
    override var basedOnObject: UUID? = null,

    @Column(name="based_on_revision", updatable = false)
    override var basedOnRevision: Int? = null,

    @AttributeOverrides(
        AttributeOverride(name = "revision",column = Column(name = "rev"))
    )
    @Embedded
    override var version: EmbeddedVersion= EmbeddedVersion(),

    var xmlLang: String = "en-GB"

) : AbstractEntity(), IBasedOn, Serializable {
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

    // @JsonBackReference(value = "agentRef")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agency_id")
    @Audited(targetAuditMode =  RelationTargetAuditMode.NOT_AUDITED)
    override lateinit var agency : Agency

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    override var changeKind: ChangeKind = ChangeKind.CREATED
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
    override var changeComment: String = ChangeKind.CREATED.description

    @JsonSerialize
    @JsonDeserialize
    @Transient
    override var classKind: String = 
        try { ElementKind.getEnum(this.javaClass.simpleName).toString() } 
        catch (e: Exception) {this.javaClass.simpleName}


    @NotAudited
    @OrderColumn(name = "owner_idx")
    @OneToMany(mappedBy = "ownerId", cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER, orphanRemoval = true)
    var comments: MutableList<Comment> = mutableListOf()

// TODO : moce these to PrePersist class

    @PrePersist
    private fun onInsert() {
        val user = SecurityContextHolder.getContext().authentication.details as User
        agency = user.agency
        if (this.xmlLang == "") user.agency.xmlLang.also { xmlLang = it }
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
                    -> if (changeComment == "") changeComment = change.description
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
            logger.error("AbstractEntityAudit::onUpdate", ex)
        }
    }

    fun makePdf(): ByteArrayOutputStream {
        val pdfOutputStream = ByteArrayOutputStream()
        try {
            PdfReport(pdfOutputStream).use { pdf ->
                fillDoc(pdf, "")
                pdf.createToc()
            }
        } catch (ex: Exception) {
            with(logger) {
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
