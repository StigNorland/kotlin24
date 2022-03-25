package no.nsd.qddt.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import no.nsd.qddt.model.*
import no.nsd.qddt.model.embedded.ElementRefQuestionItem
import no.nsd.qddt.model.embedded.UriId
import no.nsd.qddt.repository.TopicGroupRepository
import no.nsd.qddt.service.OtherMaterialService
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*


@BasePathAwareController
class TopicController(
    @Autowired repository: TopicGroupRepository,
    @Autowired val otherMaterialService: OtherMaterialService
) : AbstractRestController<TopicGroup>(repository) {

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/topicgroup/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/topicgroup/revisions/{uuid}", produces = ["application/hal+json"])
    @ResponseBody
    override fun getRevisions(
        @PathVariable uuid: UUID,
        pageable: Pageable,
    ): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/topicgroup/revisions/byparent/{uri}", produces = ["application/hal+json"])
    fun getTopics(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*> {
        logger.debug("get Study by parent rev...")
        return super.getRevisionsByParent(uri, TopicGroup::class.java, pageable)
    }


    @GetMapping("/topicgroup/pdf/{uri}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    override fun getPdf(@PathVariable uri: String): ResponseEntity<ByteArrayResource> {
        val response = super.getPdf(uri)
        logger.debug(response.body?.description ?: "#¤")
        return response
    }

    @GetMapping("/topicgroup/xml/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

    @ResponseBody
    @PostMapping("/topicgroup", produces = ["application/hal+json"])
    fun save(@RequestBody topicGroup: TopicGroup): RepresentationModel<*> {
        return entityModelBuilder(repository.saveAndFlush(topicGroup))
    }


    @Transactional(propagation = Propagation.NESTED)
    @PutMapping("/topicgroup/{uri}/children", produces = ["application/hal+json"])
    fun addConcept(
        @PathVariable uri: UUID,
        @RequestBody concept: Concept
    ): ResponseEntity<RepresentationModel<EntityModel<Concept>>> {

        repository.findById(uri).orElseThrow().let { parent ->

            parent.childrenAdd(concept)

            return ResponseEntity.ok(
                entityModelBuilder(repository.saveAndFlush(parent).children.last() as Concept)
            )
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    @PutMapping("/topicgroup/{uuid}/questionitems", produces = ["application/hal+json"])
    fun putQuestionItem(
        @PathVariable uuid: UUID,
        @RequestBody questionItem: ElementRefQuestionItem
    ): ResponseEntity<MutableList<ElementRefQuestionItem>> {

        repository.findById(uuid).orElseThrow().let { parent ->

            parent.addQuestionRef(questionItem)
//            val qRepository = repLoaderService.getRepository<QuestionItem>(ElementKind.QUESTION_ITEM)

            return ResponseEntity.ok(
                repository.saveAndFlush(parent).questionItems.map {
//                    it.element = Companion.loadRevisionEntity(it.uri, qRepository)
                    it
                }.toMutableList()
            )
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    @DeleteMapping("/topicgroup/{uuid}/questionitems/{uri}", produces = ["application/hal+json"])
    fun removeQuestionItem(
        @PathVariable uuid: UUID,
        @PathVariable uri: String
    ): ResponseEntity<MutableList<ElementRefQuestionItem>> {

        val parent = repository.findById(uuid).orElseThrow()
        val qUri = UriId.fromAny(uri)

        val qef = parent.questionItems.find { it.uri == qUri }
        if (qef != null) {
            parent.removeQuestionRef(qef)
        }

        return ResponseEntity.ok(
            repository.saveAndFlush(parent).questionItems
        )
    }

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @DeleteMapping("/topicgroup/{uuid}/otherMaterial/{filename}")
    fun removeFile( @PathVariable uuid: UUID, @PathVariable filename: String): RepresentationModel<EntityModel<TopicGroup>>  {
        try {
            val topicGroup = repository.getById(uuid)
            topicGroup.removeOtherMaterial(filename)
            val saved = repository.saveAndFlush(topicGroup)
// Due to revision concerns, we cannot delete uploads...
//                    .also{
//                    otherMaterialService.deleteFile(uuid,filename)
//                    it
//                }
            return entityModelBuilder(saved)
        } catch (ex: Exception) {
            logger.error(ex.localizedMessage)
            throw ex
        }
    }
    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @PostMapping("/topicgroup/{uuid}/otherMaterial")
    fun uploadFile( @PathVariable uuid: UUID,@RequestParam("file") file: MultipartFile): RepresentationModel<EntityModel<TopicGroup>>  {
        try {
            if (file != null) {
                val topicGroup = repository.getById(uuid)
                logger.info("got new files!!! {}",file.name)
                topicGroup.addOtherMaterial(otherMaterialService.saveFile(file, topicGroup.id!!))
                val saved = repository.saveAndFlush(topicGroup)
                return entityModelBuilder(saved)
            }
            throw Exception("No files to upload.")
        } catch (ex: Exception) {
            logger.error(ex.localizedMessage)
            throw ex
        }
    }
//    @Transactional(propagation = Propagation.NESTED)
//    @ResponseBody
//    @PostMapping("/topicgroup/{uuid}/otherMaterial",
//        headers = ["content-type=multipart/form-data"],
//        produces = ["application/hal+json"],
//        consumes = ["multipart/form-data"]
//    )
//    fun addFile(
//        @PathVariable uuid: UUID,
//        @RequestParam("files") multipartFiles: Array<MultipartFile>,
//    ): RepresentationModel<EntityModel<TopicGroup>> {
//        try {
//            if (multipartFiles != null) {
//                val topicGroup = repository.getById(uuid)
//                logger.info("got new files!!!")
//                for (file in multipartFiles) {
//                    logger.info(file.name)
//                    topicGroup.addOtherMaterial(otherMaterialService.saveFile(file, topicGroup.id!!))
//                }
//                val saved = repository.saveAndFlush(topicGroup)
//                return entityModelBuilder(saved)
//            }
//            throw Exception("No files to upload.")
//        } catch (ex: Exception) {
//            logger.error(ex.localizedMessage)
//            throw ex
//        }
//    }

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @PostMapping("/topicgroup/createfile",
        headers = ["content-type=multipart/form-data"],
        produces = ["application/hal+json"],
        consumes = ["multipart/form-data"]
    )
    fun createWithFile(
        @RequestParam("files") multipartFiles: Array<MultipartFile>?,
        @RequestParam("topicgroup") jsonString: String?
    ): RepresentationModel<EntityModel<TopicGroup>> {
        try {
            val mapper = ObjectMapper().configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false)
            val topicGroup = mapper.readValue(jsonString, TopicGroup::class.java)
            val currentuser = SecurityContextHolder.getContext().authentication.principal as User
            topicGroup.modifiedBy = currentuser
            topicGroup.agency = currentuser.agency

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
            val saved = repository.saveAndFlush(topicGroup)
            return   entityModelBuilder(saved)
        } catch (ex: Exception) {
            logger.error(ex.localizedMessage)
            throw ex
        }
    }

    fun entityModelBuilder(entity: Concept): RepresentationModel<EntityModel<Concept>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"concept")
        logger.debug("ModelBuilder Concept : {}", uriId)

        entity.children.size
        entity.authors.size
        entity.comments.size
        entity.questionItems.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
//            .link(Link.of(self))
            .link(Link.of("${baseUrl}/questionItems", "questionItems"))

            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.authors, LinkRelation.of("authors"))
            .embed(entity.children.map {
                entityModelBuilder(it as Concept)
            }, LinkRelation.of("children"))
            .build()
    }


    override fun entityModelBuilder(entity: TopicGroup): RepresentationModel<EntityModel<TopicGroup>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"topicgroup")
        logger.debug("ModelBuilder TopicGroup : {}", uriId)

        entity.children.size
        entity.authors.size
        entity.comments.size
        entity.otherMaterials.size
        entity.questionItems.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity).link(Link.of(baseUrl))
            .link(Link.of("${baseUrl}/questionItems", "questionItems"))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.authors, LinkRelation.of("authors"))
            .embed(entity.children.map {
                entityModelBuilder(it as Concept)
            }, LinkRelation.of("children"))
            .build()
    }
}
