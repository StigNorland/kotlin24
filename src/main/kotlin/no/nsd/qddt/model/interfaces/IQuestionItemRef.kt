package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.classes.AbstractEntity
import no.nsd.qddt.model.embedded.ElementRefQuestionItem

interface IQuestionItemRef {

    var changeComment: String
    var changeKind: IBasedOn.ChangeKind

    var questionItems:MutableList<ElementRefQuestionItem>

    fun addQuestionItem(qef: ElementRefQuestionItem) {
        if (this.questionItems.stream().noneMatch { cqi -> cqi == qef }) {
            questionItems.add(qef)
            this.changeKind = IBasedOn.ChangeKind.ADDED_CONTENT
            this.changeComment =  String.format("Added QI [${qef.name}]")
        } else
            AbstractEntity.logger.debug("QuestionItem not inserted, match found")
    }

    fun removeQuestionItem(qef: ElementRefQuestionItem) {
        if (questionItems.remove(qef)){
            this.changeKind = IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
            this.changeComment =  String.format("Removed QI [${qef.name}]")
        } else
            AbstractEntity.logger.debug("QuestionItem not found, nothing to do")
    }
}
