package no.nsd.qddt.config

import no.nsd.qddt.model.*
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.interfaces.IHaveChilden
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks
import org.springframework.data.rest.webmvc.support.RepositoryLinkBuilder
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelProcessor
import org.springframework.hateoas.server.mvc.BasicLinkBuilder
import org.springframework.stereotype.Component


@Component
class ModelProcessorAuditItem : RepresentationModelProcessor<EntityModel<AbstractEntityAudit>> {

    @Autowired
    lateinit var entityLinks: RepositoryEntityLinks

    override fun process(model: EntityModel<AbstractEntityAudit>): EntityModel<AbstractEntityAudit> {
        val baseUri = BasicLinkBuilder.linkToCurrentMapping()
        val entity = model.content!!
        val linkBuilder = entityLinks.linkFor(entity::class.java) as RepositoryLinkBuilder
        logger.debug(entity.version.rev.toString())
        model.addIf(
            !model.hasLink("revisions")
        ) { linkBuilder.slash("revision").slash(entity.id).withRel("revisions") }
        model.addIf(
            !model.hasLink("xml")
        ) { linkBuilder.slash(entity.id).slash("xml").withRel("xml") }
        model.addIf(
            !model.hasLink("pdf")
        ) { linkBuilder.slash(entity.id).slash("pdf").withRel("pdf") }


        return when (entity) {
            is QuestionConstruct -> {
                val uri = entity.questionId.toString()
                return model.add(Link.of("$baseUri/questionitem/$uri", "questionItem"))
            }
            is QuestionItem -> {
                val uri = entity.responseId.toString()
                return model.add(Link.of("$baseUri/responsedomain/$uri", "responseDomain"))
            }
            is ResponseDomain -> {
                val uri = UriId().also {
                    it.id = entity.id!!
                    it.rev = entity.version.rev
                }
                model
            }
            is Study -> {
                return model.add(
                    linkBuilder.slash("topics").slash(entity.id).withRel("topicGroups"),
                    linkBuilder.slash("instruments").slash(entity.id).withRel("instruments"))
            }
            is TopicGroup -> {
                return model.add(
                    linkBuilder.slash("concepts").slash(entity.id).withRel("concepts"))
            }
            else -> {
                logger.debug("FYI the entity not linkified (OK) {}", entity.name )
                model
            }
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
