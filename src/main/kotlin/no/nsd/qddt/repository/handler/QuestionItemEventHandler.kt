package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.QuestionItem
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.rest.core.annotation.HandleBeforeSave
import org.springframework.data.rest.core.annotation.RepositoryEventHandler


/**
 * @author Stig Norland
 */
//@RepositoryEventHandler(QuestionItem::class)
//class QuestionItemEventHandler {
//
//    @HandleBeforeSave
//    fun handleQuestionSave(questionItem: QuestionItem?) {
//        log.info("handleQuestionSave -> {}", questionItem?.id?:"???" )
//    }
//
//
//    companion object {
//        private val log: Logger = LoggerFactory.getLogger(QuestionItemEventHandler::class.java)
//    }
//
//}
