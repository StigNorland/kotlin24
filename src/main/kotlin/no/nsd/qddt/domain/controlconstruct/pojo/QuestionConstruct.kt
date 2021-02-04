package no.nsd.qddt.domain.controlconstruct.pojo

import no.nsd.qddt.domain.classes.elementref.*
import no.nsd.qddt.domain.classes.pdf.PdfReport
import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.domain.instruction.Instruction
import no.nsd.qddt.domain.universe.Universe
import org.hibernate.envers.Audited
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Audited
@DiscriminatorValue("QUESTION_CONSTRUCT")
class QuestionConstruct : ControlConstruct() {
    @Column(name = "description", length = 1500)
    var description: String? = null

    @Embedded
    var questionItemRef: ElementRefQuestionItem? = null

    //------------- Begin QuestionItem revision early bind "hack" ---------------
    //------------- End QuestionItem revision early bind "hack"------------------
    @ManyToMany(fetch = FetchType.EAGER)
    @OrderColumn(name = "universe_idx")
    var universe: List<Universe?>? = ArrayList(0)

    @OrderColumn(name = "instruction_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "CONTROL_CONSTRUCT_INSTRUCTION",
        joinColumns = [JoinColumn(name = "control_construct_id", referencedColumnName = "id")]
    )
    var controlConstructInstructions: List<ControlConstructInstruction?>? = ArrayList()

    @get:JsonIgnore
    val preInstructions: List<Instruction?>
        get() = controlConstructInstructions!!.stream()
            .filter { i: ControlConstructInstruction? -> i.getInstructionRank() == ControlConstructInstructionRank.PRE }
            .map { obj: ControlConstructInstruction? -> obj.getInstruction() }
            .collect(Collectors.toList())

    @get:JsonIgnore
    val postInstructions: List<Instruction?>
        get() = controlConstructInstructions!!.stream()
            .filter { i: ControlConstructInstruction? -> i.getInstructionRank() == ControlConstructInstructionRank.POST }
            .map { obj: ControlConstructInstruction? -> obj.getInstruction() }
            .collect(Collectors.toList())

    override fun beforeUpdate() {}
    override fun beforeInsert() {}
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as QuestionConstruct
        if (if (description != null) description != that.description else that.description != null) return false
        return if (questionItemRef != null) questionItemRef!!.equals(that.questionItemRef) else that.questionItemRef == null
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + if (description != null) description.hashCode() else 0
        result = 31 * result + if (questionItemRef != null) questionItemRef.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "{ " +
                "\"id\":" + (if (id == null) "null" else id) + ", " +
                "\"name\":" + (if (name == null) "null" else "\"" + name + "\"") + ", " +
                "\"description\":" + (if (description == null) "null" else "\"" + description + "\"") + ", " +
                "\"questionItemRef\":" + (if (questionItemRef == null) "null" else questionItemRef) + ", " +
                "\"modified\":" + (if (modified == null) "null" else modified) +
                "}"
    }

    override val xmlBuilder: AbstractXmlBuilder
        get() = object : ControlConstructFragmentBuilder<QuestionConstruct?>(this) {
            override fun addXmlFragments(fragments: Map<ElementKind?, Map<String?, String>>) {
                super.addXmlFragments(fragments)
                if (children.size == 0) addChildren()
                children.stream().forEach { c: AbstractXmlBuilder -> c.addXmlFragments(fragments) }
            }

            override val xmlFragment: String
                get() {
                    if (children.size == 0) addChildren()
                    return super.getXmlFragment()
                }

            private fun addChildren() {
                children.add(questionItemRef!!.getElement().getXmlBuilder())
                children.addAll(
                    universe!!.stream()
                        .map(Function<Universe?, AbstractXmlBuilder> { u: Universe? -> u.getXmlBuilder() })
                        .collect(Collectors.toList())
                )
                children.addAll(
                    controlConstructInstructions!!.stream()
                        .map(Function<ControlConstructInstruction?, AbstractXmlBuilder> { u: ControlConstructInstruction? ->
                            u.getInstruction().getXmlBuilder()
                        }).collect(Collectors.toList())
                )
            }
        }

    override fun fillDoc(pdfReport: PdfReport?, counter: String?) {
        pdfReport!!.addHeader(this, "ControlConstruct $counter")
        pdfReport.addParagraph(description!!)
        if (universe!!.size > 0) pdfReport.addheader2("Universe")
        for (uni in universe!!) {
            pdfReport.addParagraph(uni!!.description)
        }
        if (preInstructions.size > 0) pdfReport.addheader2("Pre Instructions")
        for (pre in preInstructions) {
            pdfReport.addParagraph(pre!!.description)
        }
        pdfReport.addheader2("Question Item")
        pdfReport.addParagraph(questionItemRef!!.getElement()!!.question)
        questionItemRef!!.getElement()!!.responseDomainRef.getElement()!!.fillDoc(pdfReport, "")
        if (postInstructions.size > 0) pdfReport.addheader2("Post Instructions")
        for (post in postInstructions) {
            pdfReport.addParagraph(post!!.description)
        }
        if (comments.size > 0) pdfReport.addheader2("Comments")
        pdfReport.addComments(comments)

        // pdfReport.addPadding();
    }
}
