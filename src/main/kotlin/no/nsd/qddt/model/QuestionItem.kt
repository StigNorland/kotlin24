package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.itextpdf.layout.element.Paragraph
import no.nsd.qddt.model.builder.QuestionItemFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.ParentRef
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.projection.ResponseDomainListe
import org.hibernate.Hibernate
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
@Cacheable
@Table(name = "QUESTION_ITEM")
data class QuestionItem(
  override var name: String =""
) :AbstractEntityAudit() {

  @JsonSerialize
  @Embedded
  override var basedOn: UriId? = null

  @Column(length = 2000)
  var question:String = ""

  @Column(length = 3000)
  var intent:String = ""


  @Column(insertable = false, updatable = false)
  @Embedded
  @AttributeOverrides(
    AttributeOverride(name = "id",column = Column(name = "responsedomain_id", nullable =true)),
    AttributeOverride(name = "rev",column = Column(name = "responsedomain_revision", nullable =true)),
  )
  var responseId: UriId? = null

  @Column(name="responsedomain_name")
  var responseName: String = ""

  @JsonIgnore
  @Transient
  var response: ResponseDomain? = null

  @Transient
  @JsonSerialize
  var parentRefs: MutableList<ParentRef<ConceptHierarchy>> = mutableListOf()

  override fun xmlBuilder():AbstractXmlBuilder {
    return QuestionItemFragmentBuilder(this)
  }

  override fun fillDoc(pdfReport:PdfReport, counter:String) {
    pdfReport.addHeader(this, "QuestionItem")
    pdfReport.addParagraph(this.question)
    if (intent.isNotBlank()) {
      pdfReport.addHeader2("Intent").add(Paragraph(this.intent))
    }

    response?.apply {
      fillDoc(pdfReport, "")
    }

    if (comments.size > 0) {
      pdfReport.addHeader2("Comments")
      pdfReport.addComments(comments)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    other as QuestionItem

    return id != null && id == other.id
  }

  override fun hashCode(): Int = javaClass.hashCode()

  @Override
  override fun toString(): String {
    return this::class.simpleName + "(id = $id , name = $name  , modified = $modified , classKind = $classKind )"
  }

}
