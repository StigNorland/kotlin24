package no.nsd.qddt.controller

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.model.classes.ElementOrder
import no.nsd.qddt.repository.StudyRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
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


@BasePathAwareController
class StudyController(@Autowired repository: StudyRepository) : AbstractRestController<Study>(repository) {

    @Transactional(propagation = Propagation.NESTED)
    @GetMapping("/study/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.NESTED)
    @GetMapping("/study/revisions/{uuid}", produces = ["application/hal+json"])
    override fun getRevisions(
        @PathVariable uuid: UUID,
        pageable: Pageable
    ): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/study/revisions/byparent/{uri}", produces = ["application/hal+json"])
    fun getStudies(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*> {
        logger.debug("get Study by parent rev...")
        return super.getRevisionsByParent(uri, Study::class.java, pageable)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/study/pdf/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ResponseEntity<ByteArrayResource> {
        return super.getPdf(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/study/xml/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

    @PutMapping("/study/reorder/{uuid}", produces = ["application/hal+json"])
    fun setOrder(@PathVariable uuid: UUID?, @RequestBody ranks: List<ElementOrder>): ResponseEntity<Study> {
        if (uuid!=null) {
            repository.saveAllAndFlush(
                repository.findAllById(ranks.map { it.uuid })
                    .mapIndexed { idx, study ->
                        study.parentIdx = ranks[idx].index
                        study
                    })
        } else {

        }
        return ResponseEntity.ok().build()
    }

    @Transactional(propagation = Propagation.NESTED)
    @PutMapping("/study/{uri}/children", produces = ["application/hal+json"])
    fun putStudies(
        @PathVariable uri: UUID,
        @RequestBody topicGroup: TopicGroup
    ): ResponseEntity<RepresentationModel<EntityModel<TopicGroup>>> {
        logger.debug("put studies StudyController...")

        val study = repository.findById(uri).orElseThrow()
        study.childrenAdd(topicGroup)
        val topicSaved = repository.saveAndFlush(study).children.last() as TopicGroup
        return ResponseEntity.ok(entityModelBuilder(topicSaved))
    }


    fun entityModelBuilder(it: TopicGroup): RepresentationModel<EntityModel<TopicGroup>> {
        val uriId = toUriId(it)
        val baseUrl = baseUrl(uriId,"topicgroup")
        logger.debug("ModelBuilder TopicGroup : {}", uriId)

        it.authors.size
        it.comments.size
        it.otherMaterials.size
        it.questionItems.size
        it.children.size
        Hibernate.initialize(it.agency)
        Hibernate.initialize(it.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(it)
            .link(Link.of(baseUrl))
            .link(Link.of(baseUrl+ "/children", "children"))
            .embed(it.agency!!, LinkRelation.of("agency"))
            .embed(it.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(it.comments, LinkRelation.of("comments"))
            .embed(it.authors, LinkRelation.of("authors"))
            .embed(it.otherMaterials, LinkRelation.of("otherMaterials"))
            .embed(it.questionItems, LinkRelation.of("questionItems"))
            .build()
    }


    override fun entityModelBuilder(entity: Study): RepresentationModel<EntityModel<Study>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"study")
        logger.debug("ModelBuilder Study : {}", uriId)

        entity.authors.size
        entity.comments.size
        entity.instruments.size
        entity.children.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.instruments, LinkRelation.of("instruments"))
            .embed(entity.authors, LinkRelation.of("authors"))
            .embed(entity.children.map {
                entityModelBuilder(it as TopicGroup)
            }, LinkRelation.of("children"))
            .build()
    }
}
