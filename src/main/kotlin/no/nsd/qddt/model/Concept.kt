package no.nsd.qddt.model

import no.nsd.qddt.model.builder.ConceptFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.embedded.ElementRefEmbedded
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import org.hibernate.envers.Audited
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
@DiscriminatorValue("CONCEPT")
data class Concept(override var name: String ="?") : ConceptHierarchy() {


//    @OrderColumn(name="parentIdx")
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(joinColumns = [JoinColumn(name = "parentId", referencedColumnName = "id")])
//    var questionItems: MutableList<ElementRefQuestionItem> = mutableListOf()

    @OrderColumn(name="parentIdx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CONCEPT_HIERARCHY_QUESTION_ITEM", joinColumns = [JoinColumn(name = "parentId", referencedColumnName = "id")])
    var questionItems:MutableList<ElementRefEmbedded<QuestionItem>> = mutableListOf()

    fun addQuestionItem(qef: ElementRefEmbedded<QuestionItem>) {
        if (this.questionItems.stream().noneMatch { cqi -> cqi == qef }) {
            questionItems.add(qef)
            this.changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
            this.changeComment = "QuestionItem association added"
        } else
            logger.debug("QuestionItem not inserted, match found")
    }


    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        try {
            pdfReport.addHeader(this, "Concept $counter")
            pdfReport.addParagraph(this.description)

            if (comments.size > 0) {
                pdfReport.addHeader2("Comments")
                pdfReport.addComments(comments)
            }

            if (questionItems.size > 0) {
                pdfReport.addHeader2("QuestionItem(s)")
                questionItems.stream().map {
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
        this.children
        return ConceptFragmentBuilder(this)
    }

}
