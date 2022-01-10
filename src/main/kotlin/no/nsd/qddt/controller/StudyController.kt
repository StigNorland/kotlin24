package no.nsd.qddt.controller

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.StudyRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.*
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



@BasePathAwareController
class StudyController(@Autowired repository: StudyRepository): AbstractRestController<Study>(repository) {

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/study/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String):RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/study/revisions/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: UUID, pageable: Pageable):RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/study/revisions/byparent/{uri}", produces = ["application/hal+json"])
    fun getStudies(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*>{
        logger.debug("get Study by parent rev...")
        return super.getRevisionsByParent(uri,Study::class.java, pageable)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/study/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/study/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }


    @Transactional(propagation = Propagation.NESTED)
    @PutMapping("/study/{uri}/children", produces = ["application/hal+json"])
    fun putStudies(@PathVariable uri: UUID, @RequestBody topicGroup: TopicGroup): RepresentationModel<*> {
        logger.debug("put studies StudyController...")
        var result =  repository.findById(uri).orElseThrow()
        result.addChildren(topicGroup)
        result = repository.save(result)
        if (result.children.size > 0)
            return CollectionModel.of(result.children)
//            return ResponseEntity.ok(
//                result.children.map {
//                    EntityModel.of(it,Link.of("topicgroups"))
//                })
        throw NoSuchElementException("No studies")
    }


    fun entityModelBuilder(it: TopicGroup): RepresentationModel<EntityModel<TopicGroup>> {
        logger.debug("entityModelBuilder Study TopicGroup : {}" , it.id)
        // uses size() to initialize and fetch collections
        it.authors.size
        it.comments.size
        it.otherMaterials.size
        it.questionItems.size
        it.children.size
        Hibernate.initialize(it.agency)
        Hibernate.initialize(it.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(it)
            .link(Link.of("${baseUri}/topicgroup/${it.id}"))
            .link(Link.of("${baseUri}/topicgroup/concepts/${it.id}","concepts"))
            .embed(it.agency, LinkRelation.of("agency"))
            .embed(it.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(it.comments, LinkRelation.of("comments"))
            .embed(it.authors, LinkRelation.of("authors"))
            .embed(it.otherMaterials, LinkRelation.of("otherMaterials"))
            .embed(it.questionItems, LinkRelation.of("questionItems"))
            .build()
    }


    override fun entityModelBuilder(entity: Study): RepresentationModel<EntityModel<Study>> {
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        logger.debug("entityModelBuilder Study : {}" , uriId)
        val baseUrl = if(uriId.rev != null)
            "${baseUri}/study/revision/${uriId}"
        else
            "${baseUri}/study/${uriId.id}"

        entity.children.size
        entity.authors.size
        entity.comments.size
        entity.instruments.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
//            .link(Link.of("${baseUri}/study/topics/${uriId}","topics"))

            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.instruments, LinkRelation.of("instruments"))
            .embed(entity.authors, LinkRelation.of("authors"))
            .embed(entity.children.map {
                entityModelBuilder(it as TopicGroup)
            }, LinkRelation.of("topicGroups"))
            .build()
    }
}
