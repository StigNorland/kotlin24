package no.nsd.qddt.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.itextpdf.layout.element.Paragraph
import no.nsd.qddt.model.builder.QuestionItemFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.elementref.ElementRefResponseDomain
import no.nsd.qddt.model.classes.elementref.ParentRef
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import no.nsd.qddt.model.interfaces.IDomainObjectParentRef
import org.hibernate.envers.Audited
import javax.persistence.*

/**
* Question Item is a container for Question (text) and responsedomain
* This entity introduce a breaking change into the model. it supports early binding of
* of the containing entities, by also supplying a reference to their revision number.
* This binding is outside the model which is defined here and used by the framework.
* This means that when fetching its content it will need to query the revision part of this
* system, when a revision number is specified.
*
* @author Stig Norland
*/
@Audited
@Entity
@Table(name = "QUESTION_ITEM")
class QuestionItem(override var name: String=""):AbstractEntityAudit() {

  @Column(length = 2000)
  var question:String=""

  @Column(length = 3000)
  var intent:String=""

  @AttributeOverrides(
    AttributeOverride(name = "name",column = Column(name = "responsedomain_name")),
    AttributeOverride(name = "elementId",column = Column(name = "responsedomain_id")),
    AttributeOverride(name = "elementRevision",column = Column(name = "responsedomain_revision")
    )
  )
  @Embedded
  var responseDomainRef:ElementRefResponseDomain = ElementRefResponseDomain()


  @Transient
  @JsonSerialize
  lateinit var parentRefs:List<ParentRef<IDomainObjectParentRef>>

  override val xmlBuilder:AbstractXmlBuilder
    get() =  QuestionItemFragmentBuilder(this)
  

  fun updateStatusQI() {
    this.changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
    this.changeComment = "Concept reference removed"
  }

  override fun fillDoc(pdfReport:PdfReport, counter:String) {
    pdfReport.addHeader(this, "QuestionItem")
    pdfReport.addParagraph(this.question)
    if (intent.isNotBlank()) {
      pdfReport.addHeader2("Intent").add(Paragraph(this.intent))
    }
      
    responseDomainRef.element.apply {  
      fillDoc(pdfReport, "")
    }

    if (comments.size > 0) {
      pdfReport.addHeader2("Comments")
      pdfReport.addComments(comments)
    }
  }

  override  fun beforeUpdate() {}
  override  fun beforeInsert() {}

}
