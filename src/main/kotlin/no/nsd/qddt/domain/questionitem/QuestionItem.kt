package no.nsd.qddt.domain.questionitem
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.itextpdf.layout.element.Paragraph
import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.domain.classes.elementref.ElementRefResponseDomain
import no.nsd.qddt.domain.classes.elementref.ParentRef
import no.nsd.qddt.domain.classes.pdf.PdfReport
import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.utils.StringTool
import org.hibernate.envers.Audited
import javax.persistence.*
import java.util.ArrayList
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
class QuestionItem:AbstractEntityAudit() {

  @Embedded
  var responseDomainRef:ElementRefResponseDomain = ElementRefResponseDomain()

  @Column(length = 2000)
  var question:String

  @Column(length = 3000)
  var intent:String

  @Transient
  @JsonSerialize
  var parentRefs:List<ParentRef<*>> = ArrayList<ParentRef<*>>(0)

  val xmlBuilder:AbstractXmlBuilder
  get() {
    return QuestionItemFragmentBuilder(this)
  }

  fun updateStatusQI() {
    this.setChangeKind(ChangeKind.UPDATED_HIERARCHY_RELATION)
    this.setChangeComment("Concept reference removed")
  }




  fun toString():String {
    return ("{" +
            "\"id\":" + (if (getId() == null) "null" else "\"" + getId() + "\"") + ", " +
            "\"name\":" + (if (name == null) "null" else "\"" + name + "\"") + ", " +
            "\"intent\":" + (if (intent == null) "null" else "\"" + intent + "\"") + ", " +
            "\"question\":" + (if (question == null) "null" else "\"" + question + "\"") + ", " +
            "\"responseDomainName\":" + (if (field.name == null) "null" else "\"" + field.name + "\"") + ", " +
            "\"modified\":" + (if (getModified() == null) "null" else "\"" + getModified() + "\"") + " , " +
            "\"modifiedBy\":" + (if (getModifiedBy() == null) "null" else getModifiedBy()) +
            "}")
  }
  
  fun fillDoc(pdfReport:PdfReport, counter:String) {
    pdfReport.addHeader(this, "QuestionItem")
    pdfReport.addParagraph(this.question)
    if (!StringTool.IsNullOrTrimEmpty(intent))
    {
      pdfReport.addheader2("Intent")
      .add(Paragraph(this.intent))
    }
    if (responseDomainRef.getElement() != null)
    {
      this.responseDomainRef.getElement().fillDoc(pdfReport, "")
    }
    if (comments.size() > 0)
    pdfReport.addheader2("Comments")
    pdfReport.addComments(comments)
  }
¨
}
