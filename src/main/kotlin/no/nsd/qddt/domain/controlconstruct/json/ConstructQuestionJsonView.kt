package no.nsd.qddt.domain.controlconstruct.json

import no.nsd.qddt.domain.controlconstruct.pojo.QuestionConstruct
import no.nsd.qddt.domain.questionitem.QuestionItem
import no.nsd.qddt.domain.universe.Universe
import java.util.*
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class ConstructQuestionJsonView(construct: QuestionConstruct?) : ConstructJsonView(construct) {
    /**
     * @return the questionItemUUID
     */
    var questionItemUUID: UUID? = null
    var questionItemRevision: Int? = null

    /**
     * @return the questionName
     */
    var questionName: String? = null

    /**
     * @return the questionText
     */
    var questionText: String? = null
    val universe: String

    inner class QuestionItemSimpleJson(questionItem: QuestionItem?) {
        var name: String?
        var question: String

        init {
            if (questionItem == null) return
            name = questionItem!!.name
            question = questionItem.question
        }
    }

    init {
        if (construct.getQuestionItemRef() != null) {
            questionItemUUID = construct.getQuestionItemRef().getElementId()
            questionName = construct.getQuestionItemRef().name
            questionText = construct.getQuestionItemRef().getText()
            questionItemRevision = construct.getQuestionItemRef().elementRevision
        } else {
            questionName = "?"
        }
        universe = construct.getUniverse().stream().map { s: Universe? -> s!!.description }
            .collect(Collectors.joining("/ "))
    }
}
