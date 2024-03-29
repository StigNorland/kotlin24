package no.nsd.qddt.config

import no.nsd.qddt.model.*
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.interfaces.IHaveChilden
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelProcessor
import org.springframework.hateoas.server.mvc.BasicLinkBuilder
import org.springframework.stereotype.Component
import java.util.function.Supplier
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks
import org.springframework.data.rest.webmvc.support.RepositoryLinkBuilder


@Component
class ModelProcessorAuditItem : RepresentationModelProcessor<EntityModel<AbstractEntityAudit>> {

    @Autowired
    private val entityLinks: RepositoryEntityLinks? = null

    override fun process(model: EntityModel<AbstractEntityAudit>): EntityModel<AbstractEntityAudit> {
        val baseUri = BasicLinkBuilder.linkToCurrentMapping().toString()
        val entity = model.content!!
        val linkBuilder = entityLinks?.linkFor(entity::class.java) as RepositoryLinkBuilder

        model.addIf(
            !model.hasLink("agency"),
            Supplier { linkBuilder.slash(entity.id).slash("agency").withRel("agency") }
        )
        model.addIf(
            !model.hasLink("modifiedBy"),
            Supplier { linkBuilder.slash(entity.id).slash("modifiedBy").withRel("modifiedBy") }
        )
        model.addIf(
            !model.hasLink("revisions"),
            Supplier { linkBuilder.slash(entity.id).slash("revisions").withRel("revisions") }
        )

        if (entity is IHaveChilden<*>) {
            logger.debug("entity is IHaveChilden {}", entity.name )
            model.addIf(
                !model.hasLink("children"),
                Supplier { (entityLinks?.linkFor(entity::class.java) as RepositoryLinkBuilder).slash(entity.id).slash("children").withRel("children") }
            )
        }

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
                    it.rev = entity.rev
                }
                model.add(Link.of("$baseUri/responsedomain/$uri/managedrepresentation", "managedRepresentation"))
                model.add(Link.of("$baseUri/responsedomain/$uri/xml", "Xml"))
                model.add(Link.of("$baseUri/responsedomain/$uri/pdf", "Pdf"))
            }
            else -> {
                logger.debug("entity not linkified {}", entity.name )
                model
            }
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
