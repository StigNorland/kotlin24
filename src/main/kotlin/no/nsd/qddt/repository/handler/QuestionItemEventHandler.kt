package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.QuestionItem
import org.springframework.data.rest.core.annotation.HandleBeforeSave
import org.springframework.data.rest.core.annotation.RepositoryEventHandler


/**
 * @author Stig Norland
 */
@RepositoryEventHandler(QuestionItem::class)
class QuestionItemEventHandler {

    @HandleBeforeSave
    fun handleQuestionSave(questionItem: QuestionItem?) {
    }


}
