package no.nsd.qddt.model.classes

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.itextpdf.io.source.ByteArrayOutputStream
import no.nsd.qddt.config.exception.StackTraceFilter
import no.nsd.qddt.model.Agency
import no.nsd.qddt.model.Comment
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import no.nsd.qddt.repository.handler.EntityAuditTrailListener
import org.hibernate.envers.Audited
import org.hibernate.envers.NotAudited
import org.hibernate.envers.RelationTargetAuditMode
import java.io.Serializable
import java.util.*
import java.util.stream.Collectors
import javax.persistence.*
import no.nsd.qddt.model.embedded.Version as EmbeddedVersion


/**
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
@Audited
@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
@EntityListeners(value = [EntityAuditTrailListener::class])
abstract class AbstractEntityAudit(

    @Column(insertable = false, updatable = false)
    override var agencyId: UUID? = null,

    @Column(name="based_on_object",updatable = false)
    override var basedOnObject: UUID? = null,

    @Column(name="based_on_revision", updatable = false)
    override var basedOnRevision: Int? = null,

    @Embedded
    override var version: EmbeddedVersion = EmbeddedVersion(),

    var xmlLang: String = "en-GB"

) : AbstractEntity(), IWebMenuPreview, IBasedOn,  Serializable {

    /**
     * I am the beginning of the end, and the end of time and space.
     * I am essential to creation, and I surround every place.
     * What am I?
     */

//    @Column(updatable = false, insertable = false)
//    override var rev: Int? = null



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agencyId")
    @Audited(targetAuditMode =  RelationTargetAuditMode.NOT_AUDITED)
    override lateinit var agency : Agency

    // override lateinit var agency : Agency

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
    @OrderColumn(name = "ownerIdx")
    @OneToMany(mappedBy = "ownerId", cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER, orphanRemoval = true)
    var comments: MutableList<Comment> = mutableListOf()

// TODO : moce these to PrePersist class

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

}
