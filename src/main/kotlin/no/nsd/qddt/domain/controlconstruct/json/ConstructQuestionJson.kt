package no.nsd.qddt.domain.controlconstruct.json

import no.nsd.qddt.domain.controlconstruct.pojo.QuestionConstruct
import no.nsd.qddt.domain.instruction.Instruction
import no.nsd.qddt.domain.instruction.json.InstructionJsonView
import no.nsd.qddt.domain.questionitem.QuestionItem
import no.nsd.qddt.domain.responsedomain.json.ResponseDomainJsonView
import no.nsd.qddt.domain.universe.Universe
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class ConstructQuestionJson(construct: QuestionConstruct?) : ConstructJson(construct) {
    var questionItem: QuestionItemSimpleJson? = null
    var questionItemRevision: Int? = null
    var preInstructions: List<InstructionJsonView>
    var postInstructions: List<InstructionJsonView>
    val universe: String

    inner class QuestionItemSimpleJson(questionItem: QuestionItem?) {
        var name: String?
        var question: String
        var responseDomain: ResponseDomainJsonView? = null

        init {
            if (questionItem == null) return
            name = questionItem!!.name
            question = questionItem.question
            // responseDomain =  new ResponseDomainJsonView(questionitem.getResponseDomain());
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }

    init {
        if (construct.getQuestionItemRef() != null) {
            if (construct.getQuestionItemRef().getElement() != null) questionItem =
                QuestionItemSimpleJson(construct.getQuestionItemRef().getElement())
            questionItemRevision = construct.getQuestionItemRef().elementRevision
        }
        preInstructions = construct.getPreInstructions().stream()
            .map { instruction: Instruction? -> InstructionJsonView(instruction) }
            .collect(Collectors.toList())
        postInstructions = construct.getPostInstructions().stream()
            .map { instruction: Instruction? -> InstructionJsonView(instruction) }
            .collect(Collectors.toList())
        universe = construct.getUniverse().stream().map { obj: Universe? -> obj!!.description }
            .collect(Collectors.joining("/ "))
    }
}
