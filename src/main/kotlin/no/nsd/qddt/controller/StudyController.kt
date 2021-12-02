package no.nsd.qddt.controller

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.repository.StudyRepository
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
class StudyController(@Autowired repository: StudyRepository): AbstractRestController<Study>(repository) {

    @GetMapping("/revision/study/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable):RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

    @GetMapping("/pdf/study/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/xml/study/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

    @GetMapping("/children/study/{uri}", produces = ["application/prs.hal-forms+json"])
    fun getTopicGroups(@PathVariable uri: String): RepresentationModel<*> {
        logger.debug("get studies SurveyProgramController...")
        val result = getByUri(uri).children.map {
            entityModelBuilder(it)
        }
        return CollectionModel.of(result)

    }

    @PutMapping("/children/study/{uri}", produces = ["application/hal+json"])
    fun putStudies(@PathVariable uri: UUID, @RequestBody topicGroup: TopicGroup): ResponseEntity<List<EntityModel<TopicGroup>>> {
        logger.debug("put studies StudyController...")
        val result =  repository.findById(uri).orElseThrow()
        result.addChildren(topicGroup)
        repository.saveAndFlush(result)
        if (result.children.size > 0)
            return ResponseEntity.ok(
                result.children.map {
                    EntityModel.of(it,Link.of("topicgroups"))
                })
        throw NoSuchElementException("No studies")
    }


    fun entityModelBuilder(it: TopicGroup): RepresentationModel<EntityModel<TopicGroup>> {
        it.children.size
        it.authors.size
        it.comments.size
        Hibernate.initialize(it.agency)
        Hibernate.initialize(it.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(it)
            .link(Link.of("${baseUri}/topicgroup/${it.id}"))
            .embed(it.agency, LinkRelation.of("agency"))
            .embed(it.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(it.comments, LinkRelation.of("comments"))
            .embed(it.authors, LinkRelation.of("authors"))
            .build()
    }


    override fun entityModelBuilder(entity: Study): RepresentationModel<EntityModel<Study>> {
        entity.children.size
        entity.authors.size
        entity.comments.size
        entity.instruments.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of("${baseUri}/study/${entity.id}"))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.authors, LinkRelation.of("authors"))
            .build()
    }
}
