package no.nsd.qddt.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import no.nsd.qddt.model.Concept
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.embedded.ElementRefEmbedded
import no.nsd.qddt.model.embedded.ElementRefQuestionItem
import no.nsd.qddt.model.interfaces.IElementRef
import no.nsd.qddt.repository.TopicGroupRepository
import no.nsd.qddt.service.OtherMaterialService
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
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
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*


@BasePathAwareController
class TopicController(@Autowired repository: TopicGroupRepository,
                      @Autowired val otherMaterialService: OtherMaterialService
): AbstractRestController<TopicGroup>(repository) {

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/topicgroup/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String):RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/topicgroup/revisions/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: UUID, pageable: Pageable):RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/topicgroup/revisions/byparent/{uri}", produces = ["application/hal+json"])
    fun getTopics(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*>{
        logger.debug("get Study by parent rev...")
        return super.getRevisionsByParent(uri, TopicGroup::class.java, pageable)
    }


    @GetMapping("/topicgroup/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/topicgroup/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

    @Transactional(propagation = Propagation.NESTED)
    @PutMapping("/topicgroup/{uri}/children", produces = ["application/hal+json"])
    fun putConcept(@PathVariable uri: UUID, @RequestBody  concept: Concept):  ResponseEntity<RepresentationModel<EntityModel<Concept>>> {
        logger.debug("put concept TopicController...")

        var topic =  repository.findById(uri).orElseThrow()
        topic.addChildren(concept)
        val conceptSaved = repository.saveAndFlush(topic).children.last() as Concept
        return ResponseEntity.ok(entityModelBuilder(conceptSaved))
    }

    @Transactional(propagation = Propagation.NESTED)
    @PutMapping("/topicgroup/{uri}/questionitems", produces = ["application/hal+json"])
    fun putQuestionItem(@PathVariable uri: UUID, @RequestBody  questionItem: ElementRefQuestionItem): ResponseEntity<MutableList<ElementRefQuestionItem>> {
        logger.debug("put concept TopicController...")

        var topic =  repository.findById(uri).orElseThrow()
        topic.addQuestionItem(questionItem)
        val questionItemsSaved = repository.saveAndFlush(topic).questionItems
        return ResponseEntity.ok(questionItemsSaved)
    }
    @Transactional(propagation = Propagation.NESTED)
    @PostMapping("/topicgroup/createfile",
        headers = ["content-type=multipart/form-data"],
        produces = ["application/hal+json"]
    )
    fun createWithFile(
        @RequestParam("files") multipartFiles: Array<MultipartFile>?,
        @RequestParam("topicgroup") jsonString: String?): ResponseEntity<TopicGroup>
    {
        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val topicGroup = mapper.readValue(jsonString, TopicGroup::class.java)
        if (multipartFiles != null && multipartFiles.isNotEmpty()) {
            logger.info("got new files!!!")
            if (topicGroup.id == null) {
                topicGroup.id = UUID.randomUUID()
            }
            for (file in multipartFiles) {
                logger.info(file.name)
                topicGroup.addOtherMaterial(otherMaterialService.saveFile(file, topicGroup.id!!))
            }
        }
        return ResponseEntity.ok(repository.save(topicGroup))
    }

    fun entityModelBuilder(it: Concept): RepresentationModel<EntityModel<Concept>> {
        logger.debug("entityModelBuilder TopicController : Concept")
        it.children.size
        it.authors.size
        it.comments.size
        it.questionItems.size
        Hibernate.initialize(it.agency)
        Hibernate.initialize(it.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(it)
            .link(Link.of("${baseUri}/concept/${it.id}"))
            .embed(it.agency, LinkRelation.of("agency"))
            .embed(it.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(it.comments, LinkRelation.of("comments"))
            .embed(it.authors, LinkRelation.of("authors"))
            .embed(it.questionItems, LinkRelation.of("questionItems"))
            .embed(it.children.map {
                entityModelBuilder(it as Concept)
            }, LinkRelation.of("children"))
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
        entity.otherMaterials.size
        entity.questionItems.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .link(Link.of("${baseUri}/topicgroup/${uriId.id}/questionItems","questionItems"))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.authors, LinkRelation.of("authors"))
            .embed(entity.children.map {
                entityModelBuilder(it as Concept)
            }, LinkRelation.of("children"))
            .build()
    }
}
