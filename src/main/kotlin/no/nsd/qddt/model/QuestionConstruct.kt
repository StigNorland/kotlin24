package no.nsd.qddt.model

import no.nsd.qddt.domain.classes.elementref.ElementKind
import no.nsd.qddt.model.classes.ControlConstructInstructionRank
import java.util.ArrayList
import java.util.function.Consumer
import java.util.function.Function
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity

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
    private var elementRefQuestionItem: ElementRefQuestionItem? = null

    //------------- Begin QuestionItem revision early bind "hack" ---------------
    //------------- End QuestionItem revision early bind "hack"------------------
    @ManyToMany(fetch = FetchType.EAGER)
    @OrderColumn(name = "universe_idx")
    var universe: List<Universe> = ArrayList<Universe>(0)

    @OrderColumn(name = "instruction_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "CONTROL_CONSTRUCT_INSTRUCTION",
        joinColumns = [JoinColumn(name = "control_construct_id", referencedColumnName = "id")]
    )
    var controlConstructInstructions: List<ControlConstructInstruction> = ArrayList()
    var questionItemRef: ElementRefQuestionItem?
        get() = elementRefQuestionItem
        set(elementRefQuestionItem) {
            this.elementRefQuestionItem = elementRefQuestionItem
        }

    @get:JsonIgnore
    val preInstructions: List<Any>
        get() = controlConstructInstructions.stream()
            .filter { i: ControlConstructInstruction -> i.getInstructionRank() == ControlConstructInstructionRank.PRE }
            .map(Function<ControlConstructInstruction, R?> { obj: ControlConstructInstruction -> obj.getInstruction() })
            .collect<List<Instruction>, Any>(Collectors.toList<Any>())

    @get:JsonIgnore
    val postInstructions: List<Any>
        get() = controlConstructInstructions.stream()
            .filter { i: ControlConstructInstruction -> i.getInstructionRank() == ControlConstructInstructionRank.POST }
            .map(Function<ControlConstructInstruction, R?> { obj: ControlConstructInstruction -> obj.getInstruction() })
            .collect<List<Instruction>, Any>(Collectors.toList<Any>())

    override fun beforeUpdate() {}
    override fun beforeInsert() {}
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as QuestionConstruct
        if (if (description != null) description != that.description else that.description != null) return false
        return if (elementRefQuestionItem != null) elementRefQuestionItem.equals(that.elementRefQuestionItem) else that.elementRefQuestionItem == null
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + if (description != null) description.hashCode() else 0
        result = 31 * result + if (elementRefQuestionItem != null) elementRefQuestionItem.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "{ " +
                "\"id\":" + (if (getId() == null) "null" else getId()) + ", " +
                "\"name\":" + (if (name == null) "null" else "\"" + name.toString() + "\"") + ", " +
                "\"description\":" + (if (description == null) "null" else "\"" + description + "\"") + ", " +
                "\"questionItemRef\":" + (if (elementRefQuestionItem == null) "null" else elementRefQuestionItem) + ", " +
                "\"modified\":" + (if (getModified() == null) "null" else getModified()) +
                "}"
    }

    override val xmlBuilder: AbstractXmlBuilder
        get() = object : ControlConstructFragmentBuilder<QuestionConstruct?>(this) {
            override fun addXmlFragments(fragments: Map<ElementKind?, Map<String?, String?>?>?) {
                super.addXmlFragments(fragments)
                if (children.size == 0) addChildren()
                children.stream()
                    .forEach(Consumer<AbstractXmlBuilder> { c: AbstractXmlBuilder -> c.addXmlFragments(fragments) })
            }

            val xmlFragment: String
                get() {
                    if (children.size == 0) addChildren()
                    return super.getXmlFragment()
                }

            private fun addChildren() {
                children.add(questionItemRef.getElement().getXmlBuilder())
                children.addAll(
                    universe.stream().map(Function<Universe, Any> { u: Universe -> u.getXmlBuilder() })
                        .collect(Collectors.toList<Any>())
                )
                children.addAll(
                    controlConstructInstructions.stream()
                        .map(Function<ControlConstructInstruction, Any> { u: ControlConstructInstruction ->
                            u.getInstruction().getXmlBuilder()
                        }).collect(Collectors.toList<Any>())
                )
            }
        }

    fun fillDoc(pdfReport: PdfReport, counter: String) {
        pdfReport.addHeader(this, "ControlConstruct $counter")
        pdfReport.addParagraph(description)
        if (universe.size > 0) pdfReport.addheader2("Universe")
        for (uni in universe) {
            pdfReport.addParagraph(uni.description)
        }
        if (preInstructions.size > 0) pdfReport.addheader2("Pre Instructions")
        for (pre in preInstructions) {
            pdfReport.addParagraph(pre.description)
        }
        pdfReport.addheader2("Question Item")
        pdfReport.addParagraph(questionItemRef.getElement().getQuestion())
        questionItemRef.getElement().getResponseDomainRef().getElement().fillDoc(pdfReport, "")
        if (postInstructions.size > 0) pdfReport.addheader2("Post Instructions")
        for (post in postInstructions) {
            pdfReport.addParagraph(post.description)
        }
        if (getComments().size() > 0) pdfReport.addheader2("Comments")
        pdfReport.addComments(getComments())

        // pdfReport.addPadding();
    }
}
