package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nsd.qddt.model.builder.PublicationFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.embedded.PublicationElement
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Audited
@Table(name = "PUBLICATION")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Publication(

    override var name: String = "",

    var purpose: String ="",

    var statusId: Int = 0

): AbstractEntityAudit() {


    @ManyToOne(optional = false)
    @JoinColumn(name = "statusId", updatable = false, insertable = false)
    @Audited(targetAuditMode =  RelationTargetAuditMode.NOT_AUDITED)
    lateinit var status: PublicationStatus

    @OrderColumn(name = "publication_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "PUBLICATION_ELEMENT",
        joinColumns = [JoinColumn(name = "publication_id", referencedColumnName = "id")]
    )
    var publicationElements: MutableList<PublicationElement> = mutableListOf()


    val isPublished: Boolean
        get() = status.published === PublicationStatus.Published.EXTERNAL_PUBLICATION


    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        pdfReport.addHeader(this, "Publication package")
        pdfReport.addHeader2("Purpose")
        pdfReport.addParagraph(purpose)
        pdfReport.addHeader2("Publication status")
        pdfReport.addParagraph(status.label!!)
        // pdfReport.addPadding();

        var i = 0
        publicationElements.forEach {
            (it.element as AbstractEntityAudit).fillDoc(pdfReport, counter + "." + ++i)
        }

    }

    override fun xmlBuilder(): AbstractXmlBuilder {
        return PublicationFragmentBuilder(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Publication

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , modifiedById = $modifiedById , modified = $modified , classKind = $classKind )"
    }
}
