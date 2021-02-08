package no.nsd.qddt.domain.topicgroup
import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.domain.author.Author
import no.nsd.qddt.domain.author.IAuthor
import no.nsd.qddt.domain.classes.elementref.ElementKind
import no.nsd.qddt.domain.classes.elementref.ElementRefEmbedded
import no.nsd.qddt.domain.classes.elementref.ParentRef
import no.nsd.qddt.domain.classes.interfaces.IArchived
import no.nsd.qddt.domain.classes.interfaces.IDomainObjectParentRef
import no.nsd.qddt.domain.classes.pdf.PdfReport
import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.domain.concept.Concept
import no.nsd.qddt.domain.othermaterial.OtherMaterial
import no.nsd.qddt.domain.questionitem.QuestionItem
import no.nsd.qddt.domain.study.Study
import no.nsd.qddt.utils.StringTool
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import java.util.*
import java.util.stream.Collectors
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
class TopicGroup:AbstractEntityAudit(), IAuthor, IArchived, IDomainObjectParentRef {
  @ManyToOne
  @JoinColumn(name = "study_id", updatable = false)
  var study:Study

  @Column(name = "study_id", insertable = false, updatable = false)
  protected var studyId:UUID

  @Column(name = "study_idx", insertable = false, updatable = false)
  private val studyIdx:Int

  @Column(name = "description", length = 20000)
  var description:String

  @OrderColumn(name = "concept_idx")
  @AuditMappedBy(mappedBy = "topicGroup", positionMappedBy = "conceptIdx")
  @OneToMany(mappedBy = "topicGroup", fetch = FetchType.LAZY, targetEntity = Concept::class, orphanRemoval = true, cascade = {CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE})
  var concepts: MutableList<Concept> = mutableListOf()

  // @OneToMany(fetch = FetchType.LAZY, mappedBy = "topicGroup", cascade = {CascadeType.REMOVE,CascadeType.PERSIST})
  // @OrderColumn(name="concept_idx")
  // @AuditMappedBy(mappedBy = "topicGroup", positionMappedBy ="conceptIdx")
  // private List<Concept> concepts = new ArrayList<>(0);
  @OrderColumn(name = "topicgroup_idx")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "TOPIC_GROUP_QUESTION_ITEM", joinColumns = JoinColumn(name = "topicgroup_id", referencedColumnName = "id"))
  var topicQuestionItems:List<ElementRefEmbedded<QuestionItem>> = ArrayList<ElementRefEmbedded<QuestionItem>>(0)
  @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH})
  @JoinTable(name = "TOPIC_GROUP_AUTHORS", joinColumns = {@JoinColumn(name ="topicgroup_id")}, inverseJoinColumns = {@JoinColumn(name = "author_id")})
  var authors:Set<Author> = HashSet<Author>()
  @OrderColumn(name = "owner_idx")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "TOPIC_GROUP_OTHER_MATERIAL", joinColumns = {@JoinColumn(name = "owner_id", referencedColumnName = "id")})
  var otherMaterials:List<OtherMaterial> = ArrayList<OtherMaterial>(0)
  @Transient
  private val parentRef:ParentRef<Study>
  var isArchived:Boolean = false
  set(archived) {
    field = archived
    if (archived)
    {
      LOG.info(name + " isArchived(" + getConcepts().size + ")")
      setChangeKind(ChangeKind.ARCHIVED)
      for (concept in getConcepts())
      {
        if (!concept.isArchived())
        concept.setArchived(true)
      }
    }
  }
  override val xmlBuilder:AbstractXmlBuilder
  get() {
    return TopicGroupFragmentBuilder(this)
  }
  fun addAuthor(user:Author) {
    authors.add(user)
  }
  fun addConcept(concept:Concept):Concept {
    if (concept == null) return null
    this.concepts.add(concept)
    concept.setTopicGroup(this)
    setChangeKind(ChangeKind.UPDATED_HIERARCHY_RELATION)
    setChangeComment("Concept [" + concept.name + "] added")
    return concept
  }
  fun getConcepts():List<Concept> {
    if (concepts == null) return ArrayList<Concept>(0)
    return concepts.stream()
    .filter(Predicate<Concept>({ Objects.nonNull(it) }))
    .collect(Collectors.toList<Any>())
  }
  fun setConcepts(concepts:List<Concept>) {
    this.concepts = concepts
  }
  fun addOtherMaterial(otherMaterial:OtherMaterial) {
    if (this.otherMaterials.stream().noneMatch({ cqi-> cqi.equals(otherMaterial) }))
    {
      otherMaterials.add(otherMaterial)
      this.setChangeKind(ChangeKind.UPDATED_HIERARCHY_RELATION)
      if (StringTool.IsNullOrEmpty(getChangeComment()))
      this.setChangeComment("Other material added")
    }
    else
    LOG.debug("OtherMaterial not inserted, match found")
  }
  // no update for QI when removing (it is bound to a revision anyway...).
  fun removeQuestionItem(questionItemId:UUID, rev:Int) {
    val toDelete = ElementRefEmbedded<QuestionItem>(ElementKind.QUESTION_ITEM, questionItemId, rev)
    if (topicQuestionItems.removeIf({ q-> q.equals(toDelete) }))
    {
      this.setChangeKind(ChangeKind.UPDATED_HIERARCHY_RELATION)
      this.setChangeComment("QuestionItem association removed")
    }
  }
  fun addQuestionItem(questionItemId:UUID, rev:Int) {
    addQuestionItem(ElementRefEmbedded(ElementKind.QUESTION_ITEM, questionItemId, rev))
  }
  fun addQuestionItem(qef:ElementRefEmbedded<QuestionItem>) {
    if (this.topicQuestionItems.stream().noneMatch({ cqi-> cqi.equals(qef) }))
    {
      topicQuestionItems.add(qef)
      this.setChangeKind(ChangeKind.UPDATED_HIERARCHY_RELATION)
      this.setChangeComment("QuestionItem association added")
    }
    else
    LOG.debug("ConceptQuestionItem not inserted, match found")
  }
  fun getParentRef():ParentRef<Study> {
    if (parentRef == null && study != null)
    parentRef = ParentRef(study)
    return parentRef
  }
  
  
  override fun fillDoc(pdfReport:PdfReport, counter:String) {
    pdfReport.addHeader(this, "Module " + counter)
    pdfReport.addParagraph(this.description)
    if (comments.size > 0)
    {
      pdfReport.addheader2("Comments")
      pdfReport.addComments(comments)
    }
    if (topicQuestionItems.size > 0)
    {
      pdfReport.addheader2("QuestionItem(s)")
      for (item in topicQuestionItems)
      {
        pdfReport.addheader2(item.getElement()!!.name, String.format("Version %s", item.getElement()!!.version))
        pdfReport.addParagraph(item.getElement().getQuestion())
        if (item.getElement().getResponseDomainRef().getElement() != null)
        item.getElement().getResponseDomainRef().getElement().fillDoc(pdfReport, "")
      }
    }
    pdfReport.addPadding()
    if (counter.length > 0)
    counter = counter + "."
    val i = 0
    for (concept in getConcepts())
    {
      concept.fillDoc(pdfReport, counter + ++i)
    }
  }
  @PreRemove
  fun preRemove() {
    LOG.debug("Topic pre remove")
    authors.clear()
    otherMaterials.clear()
  }
