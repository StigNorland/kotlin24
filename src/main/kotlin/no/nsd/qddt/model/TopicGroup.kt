package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.config.exception.StackTraceFilter
import no.nsd.qddt.model.builder.TopicGroupFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.embedded.ElementRefEmbedded
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IAuthorSet
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import no.nsd.qddt.model.interfaces.IOtherMaterialList
import org.hibernate.Hibernate
import org.hibernate.envers.AuditMappedBy
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
data class TopicGroup(override var name: String = "") : ConceptHierarchy(), IAuthorSet, IOtherMaterialList {
  fun getModified() : Long {
    return super.modified!!.time
  }
  @Column(insertable = false, updatable = false)
  var parentIdx: Int? = null

//  @Column(insertable = false, updatable = false)
//  var parentId: UUID? = null

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name="parentId",insertable = false, updatable = false )
  var parent: Study? = null


  @OrderColumn(name = "parentIdx")
  @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
  @OneToMany(mappedBy = "parent")
  var children: MutableList<Concept> = mutableListOf()

//  @OrderColumn(name = "ownerIdx")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "CONCEPT_HIERARCHY_OTHER_MATERIAL", joinColumns = [JoinColumn(name = "ownerId")])
  override var otherMaterials: MutableSet<OtherMaterial> = mutableSetOf()

  override fun addOtherMaterial(otherMaterial: OtherMaterial): OtherMaterial {
    return super.addOtherMaterial(otherMaterial).apply {
      changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
      if (changeComment.isBlank())
        changeComment ="Other material added"

    }
  }

  @OrderColumn(name="parentIdx")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "CONCEPT_HIERARCHY_QUESTION_ITEM", joinColumns = [JoinColumn(name = "parent_id")])
  var questionItems:MutableList<ElementRefEmbedded<QuestionItem>> = mutableListOf()

  fun addQuestionItem(qef: ElementRefEmbedded<QuestionItem>) {
    if (this.questionItems.stream().noneMatch { cqi -> cqi == qef }) {
      questionItems.add(qef)
      this.changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
      this.changeComment = "QuestionItem association added"
    } else
      logger.debug("QuestionItem not inserted, match found")
  }

  override var isArchived = false
    set(value) {
      try {
        field = value
        if (value) {
          changeKind = IBasedOn.ChangeKind.ARCHIVED

          if (Hibernate.isInitialized(children))
            logger.debug("Children isInitialized. ")
          else
            Hibernate.initialize(children)

          children.forEach{  with (it as IArchived){ if (!it.isArchived) it.isArchived = true }}
        }
      } catch (ex: Exception) {
        logger.error("setArchived", ex)
        StackTraceFilter.filter(ex.stackTrace).stream()
          .map { a -> a.toString() }
          .forEach(logger::info)
      }
    }

  override fun xmlBuilder():AbstractXmlBuilder {
    return TopicGroupFragmentBuilder(this)
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

//    pdfReport.addPadding()
//    var teller = if (counter.isNotEmpty()) "$counter." else counter
//    for ((i, concept) in concepts.withIndex()) {
//      concept.fillDoc(pdfReport, teller + (i + 1))
//    }
     var i = 0
    children.forEach {
         it.fillDoc(pdfReport, counter + "." + ++i)
     }
  }
  
//  @PreRemove
//  fun preRemove() {
//    logger.debug("Topic pre remove")
//    authors.clear()
//    otherMaterials.clear()
//  }

}
