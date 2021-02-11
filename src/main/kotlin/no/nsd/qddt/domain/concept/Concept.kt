package no.nsd.qddt.domain.concept

import com.fasterxml.jackson.annotation.JsonBackReference
import no.nsd.qddt.classes.AbstractEntityAudit
import no.nsd.qddt.classes.elementref.ElementRefEmbedded
import no.nsd.qddt.classes.interfaces.IArchived
import no.nsd.qddt.classes.interfaces.IDomainObjectParentRef
import no.nsd.qddt.classes.interfaces.IParentRef
import no.nsd.qddt.classes.pdf.PdfReport
import no.nsd.qddt.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.domain.questionitem.QuestionItem
import no.nsd.qddt.domain.topicgroup.TopicGroup
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import java.util.*
import javax.persistence.*

/**
 * <ul>
 *     <li>A Concept consist of one or more QuestionItems.</li>
 *         <li>Every QuestionItem will have a Question.</li>
 *         <li>Every QuestionItem will have a ResponseDomain.</li>
 * </ul>
 * <br>
 * ConceptScheme: Concepts express ideas associated with objects and means of representing the concept
 *
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */

@Audited
@Entity
@Table(name = "CONCEPT")
class Concept(

    var label: String="",

    override var name: String = "",

    @Column(length = 20000)
    var description: String="",

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference(value = "topicGroupRef")
    @JoinColumn(name="topicgroup_id", nullable = false,updatable = false)
    var topicGroup: TopicGroup?=null,

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name="concept_id")
    @JsonBackReference(value = "conceptParentRef")
    var parent: Concept?=null,

    // in the @OrderColumn annotation on the referencing entity.
    @Column( name = "concept_idx", insertable = false, updatable = false)
    private var conceptIdx: Long =0,


    @OrderColumn(name="concept_idx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "conceptIdx")
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, orphanRemoval = true,
        cascade = [CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE])
    var children:  MutableList<Concept> = mutableListOf(),


    @OrderColumn(name="concept_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CONCEPT_QUESTION_ITEM", joinColumns = [JoinColumn(name = "concept_id", referencedColumnName = "id")])
    var conceptQuestionItems: MutableList<ElementRefEmbedded<QuestionItem>> = mutableListOf(),

    @Transient override var parentRef: IParentRef? = null
    // override var name: String,


): AbstractEntityAudit(), IArchived, IDomainObjectParentRef {

    override var isArchived: Boolean = false

    fun removeQuestionItem(id: UUID, rev: Int) {
        conceptQuestionItems.removeIf { it.elementId == id && it.elementRevision == rev }.also {
            if (it) {
                this.changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
                this.changeComment = "QuestionItem assosiation removed"
                this.getParents().forEach{
                    it.changeKind = ChangeKind.UPDATED_CHILD
                    it.changeComment = "QuestionItem assosiation removed from child"
                }
            }
        }
    }

//        fun addQuestionItem(id: UUID,  rev: Int) {
//            addQuestionItem( ElementRefEmbedded<QuestionItem>( ElementKind.QUESTION_ITEM, id,rev ) )
//        }

    fun addQuestionItem(qef: ElementRefEmbedded<QuestionItem>) {
        this.conceptQuestionItems.stream().noneMatch{it === qef}.run {
            conceptQuestionItems.add(qef)
            changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
            changeComment = "QuestionItem assosiation added"
            getParents().forEach{ it.changeKind = ChangeKind.UPDATED_CHILD}
        }
    }

    fun addChildren(concept: Concept): Concept {
        this.children.add(concept)
        concept.parent = this
        changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
        changeComment = "SubConcept added"
        getParents().forEach{ it.changeKind = ChangeKind.UPDATED_CHILD}
        return concept
    }


    fun hasTopicGroup(): Boolean {
            return (topicGroup != null)
        }

    private fun getParents(): MutableList<AbstractEntityAudit> {
        var  current = this
        return sequence<AbstractEntityAudit> {
            while (current.parent != null) {
                current = current.parent!!
                yield(current)
            }
            if (current.topicGroup != null)
                yield(current.topicGroup!!)
        }.toMutableList()
    }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        try {
            pdfReport.addHeader(this, "Concept $counter")
            pdfReport.addParagraph(this.description)

            if (comments.size > 0) {
                pdfReport.addHeader2("Comments")
                pdfReport.addComments(comments)
            }

            if (conceptQuestionItems.size > 0) {
                pdfReport.addHeader2("QuestionItem(s)")
                conceptQuestionItems.stream().map {
                    it.element
                }
                    .forEach {
                        if (it != null) {
                            pdfReport.addHeader2(it.name, String.format("Version %s", it.version))
                            pdfReport.addParagraph(it.question)
                            it.responseDomainRef.element?.fillDoc(pdfReport, "")
                        }
                    }
            }
            pdfReport.addPadding()

            var i = 0
            children.forEach {
                it.fillDoc(pdfReport, counter + "." + ++i)
            }

            if (children.size == 0)
                pdfReport.addPadding()

        } catch (ex:Exception) {
            logger.error(ex.message)
            throw ex
        }
    }

    override fun beforeUpdate() {
        TODO("Not yet implemented")
    }

    override fun beforeInsert() {
        TODO("Not yet implemented")
    }

    override val xmlBuilder: AbstractXmlBuilder
        get() = ConceptFragmentBuilder(this)

}
