package no.nsd.qddt.model

import no.nsd.qddt.model.builder.TopicGroupFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.embedded.ElementRefEmbedded
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IAuthorSet
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import no.nsd.qddt.model.interfaces.IOtherMaterialList
import org.hibernate.envers.Audited
import java.util.*
import javax.persistence.*

/**
 * <ul class="inheritance">
 * 	<li>A Topic Group (Module) will have one or more Concepts.
 * 		<ul class="inheritance">
 * 			<li>A Concept consist of one or more QuestionItems.
 * 				<ul class="inheritance">
 * 					<li>Every QuestionItem will have a Question.</li>
 * 				</ul>
 * 				<ul class="inheritance">
 * 					<li>Every QuestionItem will have a ResponseDomain.</li>
 * 				</ul>
 * 			</li>
 * 		</ul>
 * 	</li>
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
@DiscriminatorValue("TOPIC_GROUP")
class TopicGroup : ConceptHierarchy(), IAuthorSet, IOtherMaterialList {

  override var name: String = ""

//  @Column(length = 20000)
//  var description:String=""

  // @OrderColumn(name = "topicgroup_idx")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable
  var questionItems:MutableList<ElementRefEmbedded<QuestionItem>> = mutableListOf()

  @ManyToMany()
  @JoinTable(
    name = "CONCEPT_HIERARCHY_AUTHORS",
    joinColumns = [JoinColumn(name = "parent_id", referencedColumnName = "id")],
    inverseJoinColumns = [JoinColumn(name = "author_id", referencedColumnName = "id")]
  )  override var authors: MutableSet<Author> = mutableSetOf()

  @OrderColumn(name = "ownerIdx")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "TOPIC_GROUP_OTHER_MATERIAL", joinColumns = [JoinColumn(name = "ownerId", referencedColumnName = "id")])
  override var otherMaterials: MutableList<OtherMaterial> = mutableListOf()


  override fun xmlBuilder():AbstractXmlBuilder {
    return TopicGroupFragmentBuilder(this)
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
    if (questionItems.removeIf { q -> q == toDelete })
    {
      this.changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
      this.changeComment = "QuestionItem association removed"
    }
  }

  fun addQuestionItem(questionItemId:UUID, rev:Int) {
    addQuestionItem(ElementRefEmbedded(ElementKind.QUESTION_ITEM, questionItemId, rev))
  }
 
  fun addQuestionItem(qef: ElementRefEmbedded<QuestionItem>) {
    if (this.questionItems.stream().noneMatch { cqi -> cqi == qef }) {
      questionItems.add(qef)
      this.changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
      this.changeComment = "QuestionItem association added"
    } else
    logger.debug("ConceptQuestionItem not inserted, match found")
  }

  
  override fun fillDoc(pdfReport:PdfReport, counter:String) {
    pdfReport.addHeader(this, "Module $counter")
    pdfReport.addParagraph(this.description)


    if (comments.size > 0)
    {
      pdfReport.addHeader2("Comments")
      pdfReport.addComments(comments)
    }
    if (questionItems.size > 0)
    {
      pdfReport.addHeader2("QuestionItem(s)")
      questionItems.stream()
        .filter { it.element != null }
        .map { it.element!! }
        .forEach {
          pdfReport.run {
            addHeader2(it.name, String.format("Version %s", it.version))
            addParagraph(it.question)
          }
          if (it.responseDomain != null)
            it.responseDomain.apply {
              fillDoc(pdfReport, "")
            }
        }
    }

    pdfReport.addPadding()

    // var i = 0
    // concepts.forEach {
    //     it.fillDoc(pdfReport, counter + "." + ++i)
    // }
  }
  
//  @PreRemove
//  fun preRemove() {
//    logger.debug("Topic pre remove")
//    authors.clear()
//    otherMaterials.clear()
//  }

}
