package no.nsd.qddt.config

import no.nsd.qddt.model.QuestionItem
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelProcessor
import org.springframework.hateoas.server.mvc.BasicLinkBuilder
import org.springframework.stereotype.Component


/**
 * A [RepresentationModelProcessor] that takes an [Order] that has been wrapped by Spring Data REST into an
 * [EntityModel] and applies custom Spring HATEAOS-based [Link]s based on the state.
 * @author Stig Norland
 */
@Component
class ModelProcessorQuestionItem : RepresentationModelProcessor<EntityModel<QuestionItem>> {

    override fun process(model: EntityModel<QuestionItem>): EntityModel<QuestionItem> {
        val baseUri = BasicLinkBuilder.linkToCurrentMapping().toString()
        val uri = model.content?.responseId.toString()

        return model.add(Link.of("$baseUri/responsedomain/$uri", "responseDomain"))
    }
}
