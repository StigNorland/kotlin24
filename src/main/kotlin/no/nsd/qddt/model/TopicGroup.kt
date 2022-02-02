package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.model.builder.TopicGroupFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.ParentRef
import no.nsd.qddt.model.embedded.ElementRefQuestionItem
import no.nsd.qddt.model.interfaces.*
import org.hibernate.Hibernate
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
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
data class TopicGroup(override var name: String = "") :ConceptHierarchy(), IAuthorSet, IArchived, IOtherMaterialList,
  IQuestionItemRef {

  @Column(insertable = false, updatable = false)
  var parentIdx: Int? = null

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  override lateinit var parent: ConceptHierarchy

  @JsonIgnore
  @Transient
  override var parentRef: IParentRef? = null
    get() = ParentRef(parent)

  @OrderColumn(name = "parentIdx")
  @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
  @OneToMany(mappedBy = "parent", cascade = [CascadeType.PERSIST, CascadeType.MERGE], targetEntity = Concept::class)
  override var children: MutableList<ConceptHierarchy> = mutableListOf()


//  @OrderColumn(name = "ownerIdx")
  @ElementCollection()
  @CollectionTable(name = "CONCEPT_HIERARCHY_OTHER_MATERIAL", joinColumns = [JoinColumn(name = "owner_id")])
  override var otherMaterials: MutableList<OtherMaterial> = mutableListOf()

  @OrderColumn(name="parentIdx")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "CONCEPT_HIERARCHY_QUESTION_ITEM",joinColumns = [JoinColumn(name = "parent_id", referencedColumnName = "id")])
  override var questionItems:MutableList<ElementRefQuestionItem> = mutableListOf()

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

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    other as TopicGroup

    return id != null && id == other.id
  }

  override fun hashCode(): Int = javaClass.hashCode()

  @Override
  override fun toString(): String {
    return this::class.simpleName + "(id = $id , name = $name , modifiedById = $modifiedById , modified = $modified , classKind = $classKind )"
  }

}
