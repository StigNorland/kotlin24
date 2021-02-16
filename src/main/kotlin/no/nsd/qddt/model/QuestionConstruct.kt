package no.nsd.qddt.model

import no.nsd.qddt.model.builder.ControlConstructFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.enums.ControlConstructInstructionRank
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.embedded.ElementRefQuestionItem
import org.hibernate.envers.Audited
import javax.persistence.*
import kotlin.streams.toList

/**
 * @author Stig Norland
 */
@Entity
@Audited
@DiscriminatorValue("QUESTION_CONSTRUCT")
class QuestionConstruct : ControlConstruct() {
    @Column(name = "description", length = 1500)
    var description: String? = null

    @AttributeOverrides(
        AttributeOverride(name = "name",column = Column(name = "question_name", length = 25)),
        AttributeOverride(name = "text",column = Column(name = "question_text", length = 500)),
        AttributeOverride(name = "elementId",column = Column(name = "questionitem_id")),
        AttributeOverride(name = "elementRevision",column = Column(name = "questionitem_revision")),
        AttributeOverride(name = "version.revision",column = Column(name = "questionitem_revision"))
    )
    @Embedded
    var questionItemRef: ElementRefQuestionItem? = null

    
    @ManyToMany(fetch = FetchType.EAGER)
    @OrderColumn(name = "universe_idx")
    var universe: MutableList<Universe> = mutableListOf()

    @OrderColumn(name = "instruction_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "CONTROL_CONSTRUCT_INSTRUCTION",
        joinColumns = [JoinColumn(name = "control_construct_id", referencedColumnName = "id")]
    )
    var controlConstructInstructions: MutableList<ControlConstructInstruction> = mutableListOf()

    val preInstructions
        get() = controlConstructInstructions.stream()
            .filter { it.instructionRank == ControlConstructInstructionRank.PRE }
            .map {it.instruction }
            .toList()
    
    val postInstructions
        get() = controlConstructInstructions.stream()
            .filter { it.instructionRank == ControlConstructInstructionRank.POST }
            .map { it.instruction}
            .toList()

    override fun beforeUpdate() {}
    override fun beforeInsert() {}
    

    override val xmlBuilder: AbstractXmlBuilder
        get() = object : ControlConstructFragmentBuilder<QuestionConstruct>(this) {
            override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {
                super.addXmlFragments(fragments)
                if (children.size == 0) addChildren()
                children.stream()
                    .forEach { it.addXmlFragments(fragments) }
            }

            override val xmlFragment: String
                get() {
                    if (children.size == 0) addChildren()
                    return super.xmlFragment
                }

            private fun addChildren() {
                questionItemRef?.element?.let { children.add(it.xmlBuilder) }
                children.addAll(universe.stream()
                    .map { it.xmlBuilder }.toList()
                )
                
                children.addAll(controlConstructInstructions.stream()
                    .map { it .instruction.xmlBuilder }.toList()
                )
            }
        }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        pdfReport.addHeader(this, "ControlConstruct $counter")
        description?.let { pdfReport.addParagraph(it) }

        if (universe.size > 0)
            pdfReport.addHeader2("Universe")
        for (uni in universe) {
            pdfReport.addParagraph(uni.description)
        }

        if (preInstructions.isNotEmpty())
            pdfReport.addHeader2("Pre Instructions")

        for (pre in preInstructions) {
            pre.description?.let { pdfReport.addParagraph(it) }
        }

        pdfReport.addHeader2("Question Item")
        questionItemRef?.name?.let { pdfReport.addParagraph(it) }
        questionItemRef?.element?.responseDomainRef?.element?.fillDoc(pdfReport, "")

        if (postInstructions.isNotEmpty())
            pdfReport.addHeader2("Post Instructions")

        for (post in postInstructions) {
            post.description?.let { pdfReport.addParagraph(it) }
        }
        if (comments.size > 0)
            pdfReport.addHeader2("Comments")

        pdfReport.addComments(comments)
    }
}
