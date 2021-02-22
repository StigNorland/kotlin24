package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonBackReference
import no.nsd.qddt.model.builder.TopicGroupFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.embedded.ElementRefEmbedded
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.*
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import java.util.*
import javax.persistence.*

/**
*
* <ul class="inheritance">
* <li>A Topic Group (Module) will have one or more Concepts.
* <ul class="inheritance">
* <li>A Concept consist of one or more QuestionItems.
* <ul class="inheritance">
* <li>Every QuestionItem will have a Question.</li>
* </ul>
* <ul class="inheritance">
* <li>Every QuestionItem will have a ResponseDomain.</li>
* </ul>
* </li>
* </ul>
* </li>
* </ul>
* <br>
* A Topic Group (Module) should be a collection of QuestionItems and Concepts that has a theme that is broader than a Concept.
* All QuestionItems that doesn't belong to a specific Concept, will be collected in a default Concept that
* every Module should have. This default Concept should not be visualized as a Concept, but as a
* "Virtual Topic Group (Module)". The reason for this is a simplified data model.
*
* @author Stig Norland
* @author Dag Østgulen Heradstveit
*/
@Audited
@Entity
@Table(name = "TOPIC_GROUP")
class TopicGroup:AbstractEntityAudit(), IAuthorSet, IOtherMaterialList, IArchived, IDomainObjectParentRef {

  var label: String=""

  override lateinit var name: String

  @Column(length = 20000)
  var description:String?=null

  @Column(name = "study_id", insertable = false, updatable = false)
  protected val studyId:UUID?=null

  @ManyToOne
  @JsonBackReference(value = "studyRef")
  @JoinColumn(name = "study_id", updatable = false)
  var study: Study? = null

  @Column(name = "study_idx", insertable = false, updatable = false)
  private var studyIdx:Int?=null


  @OrderColumn(name = "concept_idx")
  @AuditMappedBy(mappedBy = "topicGroup", positionMappedBy = "conceptIdx")
  @OneToMany(mappedBy = "topicGroup", fetch = FetchType.LAZY, targetEntity = Concept::class, orphanRemoval = true,
    cascade = [CascadeType.REMOVE,CascadeType.PERSIST])
  var concepts: MutableList<Concept> = mutableListOf()

  @OrderColumn(name = "topicgroup_idx")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "TOPIC_GROUP_QUESTION_ITEM", joinColumns = [JoinColumn(name = "topicgroup_id",referencedColumnName = "id")] )
  var topicQuestionItems:MutableList<ElementRefEmbedded<QuestionItem>> = mutableListOf()

  @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.DETACH])
  @JoinTable(name = "TOPIC_GROUP_AUTHORS", joinColumns = [JoinColumn(name = "topicgroup_id")], inverseJoinColumns = [JoinColumn(name = "author_id")])
  override var authors:MutableSet<Author> = mutableSetOf()

  @OrderColumn(name = "owner_idx")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "TOPIC_GROUP_OTHER_MATERIAL", joinColumns = [JoinColumn(name = "owner_id", referencedColumnName = "id")])
  override var otherMaterials: MutableList<OtherMaterial> = mutableListOf()

  @Transient
  override var parentRef: IParentRef? = null


  override var isArchived:Boolean = false
  set(value) {
    field = value
    if (value){
      logger.info(name + " isArchived(" + concepts.size + ")")
      changeKind = ChangeKind.ARCHIVED
      this.concepts.forEach { 
        if (!it.isArchived)
          it.isArchived = true
      }
    }
  }


  override fun xmlBuilder():AbstractXmlBuilder {
    return TopicGroupFragmentBuilder(this)
  }
  
  
  fun addConcept(concept: Concept): Concept {
    this.concepts.add(concept)
    concept.topicGroup = this
    changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
    changeComment = "Concept [" + concept.name + "] added"
    return concept
  }

  override fun addOtherMaterial(otherMaterial: OtherMaterial): OtherMaterial {
    return super.addOtherMaterial(otherMaterial).apply {
      changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
      if (changeComment.isBlank())
        changeComment ="Other material added"

    }
  }

  // no update for QI when removing (it is bound to a revision anyway...).
  fun removeQuestionItem(questionItemId:UUID, rev:Int) {
    val toDelete = ElementRefEmbedded<QuestionItem>(ElementKind.QUESTION_ITEM, questionItemId, rev)
    if (topicQuestionItems.removeIf { q -> q == toDelete })
    {
      this.changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
      this.changeComment = "QuestionItem association removed"
    }
  }

  fun addQuestionItem(questionItemId:UUID, rev:Int) {
    addQuestionItem(ElementRefEmbedded(ElementKind.QUESTION_ITEM, questionItemId, rev))
  }
 
   fun addQuestionItem(qef: ElementRefEmbedded<QuestionItem>) {
    if (this.topicQuestionItems.stream().noneMatch { cqi -> cqi.equals(qef) }) {
      topicQuestionItems.add(qef)
      this.changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
      this.changeComment = "QuestionItem association added"
    } else
    logger.debug("ConceptQuestionItem not inserted, match found")
  }
  
  
  override fun fillDoc(pdfReport:PdfReport, counter:String) {
    pdfReport.addHeader(this, "Module $counter")
    pdfReport.addParagraph(this.description?:"?")


    if (comments.size > 0)
    {
      pdfReport.addHeader2("Comments")
      pdfReport.addComments(comments)
    }
    if (topicQuestionItems.size > 0)
    {
      pdfReport.addHeader2("QuestionItem(s)")
      topicQuestionItems.stream()
        .filter { it.element != null }
        .map { it.element!! }
        .forEach {
          pdfReport.run {
            addHeader2(it.name, String.format("Version %s", it.version))
            addParagraph(it.question)
          }
          if (it.responseDomainRef.element != null)
            it.responseDomainRef.element.apply {
              fillDoc(pdfReport, "")
            }
        }
    }

    pdfReport.addPadding()

    var i = 0
    concepts.forEach {
        it.fillDoc(pdfReport, counter + "." + ++i)
    }
  }
  
  @PreRemove
  fun preRemove() {
    logger.debug("Topic pre remove")
    authors.clear()
    otherMaterials.clear()
  }

}
