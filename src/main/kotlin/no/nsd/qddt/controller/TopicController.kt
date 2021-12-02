package no.nsd.qddt.controller

import no.nsd.qddt.model.Concept
import no.nsd.qddt.model.Study
import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.StudyRepository
import no.nsd.qddt.repository.TopicGroupRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*


@Transactional(propagation = Propagation.REQUIRED)
@BasePathAwareController
class TopicController(@Autowired repository: TopicGroupRepository): AbstractRestController<TopicGroup>(repository) {

    @GetMapping("/topicgroup/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

    @GetMapping("/topicgroup/pdf/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/topicgroup/xml/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

    @GetMapping("/topicgroup/concepts/{uri}", produces = ["application/prs.hal-forms+json"])
    fun getConcept(@PathVariable uri: String): RepresentationModel<*> {
        logger.debug("get studies SurveyProgramController...")
        val result = getByUri(uri).children.map {
            entityModelBuilder(it)
        }
        return CollectionModel.of(result)

    }

    @PutMapping("/topicgroup/concepts/{uri}", produces = ["application/hal+json"])
    fun putConcept(@PathVariable uri: UUID, @RequestBody concept: Concept): ResponseEntity<List<EntityModel<Concept>>> {
        logger.debug("put concept TopicController...")
        val result =  repository.findById(uri).orElseThrow()
        result.addChildren(concept)
        repository.saveAndFlush(result)
        if (result.children.size > 0)
            return ResponseEntity.ok(
                result.children.map {
                    EntityModel.of(it, Link.of("concepts"))
                })
        throw NoSuchElementException("No concepts")
    }


    fun entityModelBuilder(it: Concept): RepresentationModel<EntityModel<Concept>> {
        it.children.size
        it.authors.size
        it.comments.size
        Hibernate.initialize(it.agency)
        Hibernate.initialize(it.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(it)
            .link(Link.of("${baseUri}/concept/${it.id}"))
            .embed(it.agency, LinkRelation.of("agency"))
            .embed(it.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(it.comments, LinkRelation.of("comments"))
            .embed(it.authors, LinkRelation.of("authors"))
            .embed(it.children, LinkRelation.of("children"))
            .build()
    }


    override fun entityModelBuilder(entity: TopicGroup): RepresentationModel<EntityModel<TopicGroup>> {
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        logger.debug("entityModelBuilder TopicController : {}" , uriId)
        val baseUrl = if(uriId.rev != null)
            "${baseUri}/topicgroup/revision/${uriId}"
        else
            "${baseUri}/topicgroup/${uriId.id}"
        entity.children.size
        entity.authors.size
        entity.comments.size
        entity.otherMaterials
        entity.questionItems
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.authors, LinkRelation.of("authors"))
            .embed(entity.otherMaterials, LinkRelation.of("otherMaterials"))
            .embed(entity.questionItems, LinkRelation.of("questionItems"))
            .embed(entity.children.map {
                entityModelBuilder(it)
            }, LinkRelation.of("concepts"))
            .build()
    }
}
