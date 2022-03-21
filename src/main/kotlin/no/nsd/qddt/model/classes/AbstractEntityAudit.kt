package no.nsd.qddt.model.classes

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.itextpdf.io.source.ByteArrayOutputStream
import no.nsd.qddt.config.exception.StackTraceFilter
import no.nsd.qddt.model.Agency
import no.nsd.qddt.model.Comment
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.embedded.UriId
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import no.nsd.qddt.repository.handler.EntityAuditTrailListener
import org.hibernate.envers.Audited
import org.hibernate.envers.NotAudited
import org.hibernate.envers.RelationTargetAuditMode
import java.io.Serializable
import java.sql.Timestamp
import java.util.stream.Collectors
import javax.persistence.*
import no.nsd.qddt.model.embedded.Version as EmbeddedVersion

/**
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */
@Audited
@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true, value = ["hibernateLazyInitializer", "handler"])
@EntityListeners(value = [EntityAuditTrailListener::class])
abstract class AbstractEntityAudit(

//    @JsonIgnore
//    @Column(insertable = false, updatable = false)
//    protected var agencyId: UUID? = null,
    @JsonIgnoreProperties("surveyPrograms","users")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "AGENCY_ID", updatable = false, nullable = false)
    @Audited(targetAuditMode =  RelationTargetAuditMode.NOT_AUDITED)
    override  var agency : Agency?=null,

    @AttributeOverrides(AttributeOverride(name = "rev",column = Column(name = "rev")))
    @Embedded
    override var version: EmbeddedVersion = EmbeddedVersion(),

    var xmlLang: String = "en-GB"

) : AbstractEntity(), IWebMenuPreview, IBasedOn,  Serializable {

    @JsonSerialize
    @JsonDeserialize
    @AttributeOverrides(
        AttributeOverride(name = "id",column = Column(name = "based_on_object")),
        AttributeOverride(name = "rev",column = Column(name = "based_on_revision" )),
    )
    @Embedded
    override var basedOn : UriId?= null

    /**
     * I am the beginning of the end, and the end of time and space.
     * I am essential to creation, and I surround every place.
     * What am I?
     */


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

    @JsonIgnore
    @NotAudited
    @OrderColumn(name = "ownerIdx")
    @OneToMany(mappedBy = "ownerId", cascade = [CascadeType.REMOVE], fetch = FetchType.LAZY, orphanRemoval = true)
    var comments: MutableList<Comment> = mutableListOf()

    @JsonFormat
        (shape = JsonFormat.Shape.NUMBER_INT)
    override var modified: Timestamp? = null

// TODO : move these to PrePersist class

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
