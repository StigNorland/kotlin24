package no.nsd.qddt.model

import no.nsd.qddt.model.builder.ConceptFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.embedded.ElementRefQuestionItem
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
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
class Concept : AbstractEntityAudit(), IArchived {

    var label: String=""

    override var name: String = ""

    @Column(length = 20000)
    var description: String=""

    override var isArchived: Boolean = false

    /**---------------------------------------------
     *    Parent ref
    ----------------------------------------------**/

    @Column(insertable = false, updatable = false)
    var conceptIdx: Int? = null

    @Column(insertable = false, updatable = false)
    var conceptId:UUID? = null

    @Column(insertable = false, updatable = false)
    var topicgroupId:UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="topicgroupId")
    var topicGroup: TopicGroup? = null

    /**---------------------------------------------
     *    Children refs
    ----------------------------------------------**/

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="conceptId")
//    var parent: Concept? = null

    //    @PrimaryKeyJoinColumn
//    @OrderColumn(name="conceptIdx")
//    @AuditMappedBy(mappedBy = "conceptId", positionMappedBy = "conceptIdx")
    @OneToMany(mappedBy = "conceptId",cascade = [CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE])
    var children: MutableList<Concept> = mutableListOf()


    // @OrderColumn(name="conceptIdx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CONCEPT_QUESTION_ITEM", joinColumns = [JoinColumn(name = "conceptId", referencedColumnName = "id")])
    var conceptQuestionItems: MutableList<ElementRefQuestionItem> = mutableListOf()


    fun removeQuestionItem(id: UUID, rev: Int) {
        conceptQuestionItems.removeIf { it.elementId == id && it.version.rev == rev }.also { doIt ->
            if (doIt) {
                this.changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
                this.changeComment = "QuestionItem assosiation removed"
                // this.myParents().forEach{
                //     it.changeKind = ChangeKind.UPDATED_CHILD
                //     it.changeComment = "QuestionItem assosiation removed from child"
                // }
            }
        }
    }


    fun addQuestionItem(qef: ElementRefQuestionItem) {
        this.conceptQuestionItems.stream().noneMatch{it === qef}.run {
            conceptQuestionItems.add(qef)
            changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
            changeComment = "QuestionItem assosiation added"
            // myParents().forEach{ it.changeKind = ChangeKind.UPDATED_CHILD}
        }
    }

//    fun addChildren(concept: Concept): Concept {
//        this.children.add(concept)
//        concept.parent = this
//        changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
//        changeComment = "SubConcept added"
//        // myParents().forEach{ it.changeKind = ChangeKind.UPDATED_CHILD}
//        return concept
//    }


    // private fun myParents(): List<AbstractEntityAudit> {
    //     var  current: AbstractEntityAudit = this
    //     return sequence<AbstractEntityAudit>() {
    //         while (current.parent != null) {
    //             current = current.parent
    //             yield(current as AbstractEntityAudit)
    //         }
    //         if (current.topicGroup != null)
    //             yield(current.topicGroup as AbstractEntityAudit)
    //     }.toList()
    // }

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
                            it.responseDomain?.fillDoc(pdfReport, "")
                        }
                    }
            }
            pdfReport.addPadding()

//            var i = 0
//            children.forEach {
//                it.fillDoc(pdfReport, counter + "." + ++i)
//            }
//
//            if (children.size == 0)
//                pdfReport.addPadding()

        } catch (ex:Exception) {
            logger.error(ex.message)
            throw ex
        }
    }

    override fun xmlBuilder(): AbstractXmlBuilder {
        return ConceptFragmentBuilder(this)
    }

}
