package no.nsd.qddt.model

import no.nsd.qddt.model.builder.PublicationFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.embedded.PublicationElement
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Audited
@Table(name = "PUBLICATION")
class Publication : AbstractEntityAudit() {

    lateinit var purpose: String

    override lateinit var name: String

    @Column(name = "status_id", insertable = false, updatable = false)
    private var statusId: Long? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JoinColumn(name = "status_id")
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
        pdfReport.addParagraph(status.label)
        // pdfReport.addPadding();

        var i = 0
        publicationElements.forEach {
            (it.element as AbstractEntityAudit).fillDoc(pdfReport, counter + "." + ++i)
        }

    }

    override fun xmlBuilder(): AbstractXmlBuilder {
        return PublicationFragmentBuilder(this)
    }
}
