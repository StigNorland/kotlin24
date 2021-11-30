package no.nsd.qddt.config

import no.nsd.qddt.model.QuestionConstruct
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.Study
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
        model.addIf(
            !model.hasLink("revisions")
        ) {
            logger.debug("${baseUri}:${linkBuilder.toUri().fragment}")
            Link.of("$baseUri/revisions/surveyprogram/${entity.id}", "revisions")
//            linkBuilder.slash(entity.id).slash("revisions").withRel("revisions")
        }
        model.addIf(
            !model.hasLink("xml")
        ) { linkBuilder.slash(entity.id).slash("xml").withRel("xml") }
        model.addIf(
            !model.hasLink("pdf")
        ) { linkBuilder.slash(entity.id).slash("pdf").withRel("pdf") }

//        if (entity is IHaveChilden<*>) {
//            logger.debug("entity is IHaveChilden {}", entity.name )
//            model.addIf(
//                !model.hasLink("children2")
//            ) {
//                (entityLinks.linkFor(entity::class.java) as RepositoryLinkBuilder)
//                    .slash(entity.id).slash("children")
//                    .withRel("children2")
//            }
//        }

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
                    linkBuilder.slash(entity.id).slash("topics").withRel("topics"),
                    linkBuilder.slash(entity.id).slash("instruments").withRel("instruments"))
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
