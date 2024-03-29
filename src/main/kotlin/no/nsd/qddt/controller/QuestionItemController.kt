package no.nsd.qddt.controller

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.repository.QuestionItemRepository
import no.nsd.qddt.repository.ResponseDomainRepository
import no.nsd.qddt.repository.handler.EntityAuditTrailListener
import no.nsd.qddt.repository.projection.ManagedRepresentation
import no.nsd.qddt.repository.projection.UserListe
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.projection.ProjectionFactory
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*


@BasePathAwareController
class QuestionItemController(@Autowired repository: QuestionItemRepository): AbstractRestController<QuestionItem>(repository) {


    @Autowired
    private val factory: ProjectionFactory? = null

    @Autowired
    private val responseDomainRepository: ResponseDomainRepository? = null

    @Transactional(propagation = Propagation.NESTED)
    @GetMapping("/questionitem/revision/{uri}", produces = ["application/hal+json"])
    @ResponseBody
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.NESTED)
    @GetMapping("/questionitem/revisions/{uuid}", produces = ["application/hal+json"])
    @ResponseBody
    override fun getRevisions(@PathVariable uuid: UUID,pageable: Pageable): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
    }


    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/questionitem/{uuid}", produces = ["application/hal+json"])
    fun getById(@PathVariable uuid: UUID): RepresentationModel<*> {
        return entityModelBuilder(repository.getById(uuid))
    }

    @Transactional(propagation = Propagation.NESTED)
    @GetMapping("/questionitem/pdf/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ResponseEntity<ByteArrayResource> {
        return super.getPdf(uri)
    }

    @Transactional(propagation = Propagation.NESTED)
    @GetMapping("/questionitem/xml/{uri}", produces = [MediaType.TEXT_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @PutMapping("/questionitem/{uuid}",produces = ["application/hal+json", "application/text"], consumes = ["application/hal+json","application/json"])
    fun putQuestionItem(@PathVariable uuid: UUID, @RequestBody questionItem: QuestionItem): ResponseEntity<*> {

        try {
            if (questionItem.responseName.isBlank() &&(questionItem.responseId != null) && (this.responseDomainRepository != null)) {
                val response =loadRevisionEntity(questionItem.responseId!!, repository = this.responseDomainRepository)
                questionItem.responseName = response.name
            }

            val saved = repository.save(questionItem)

            return ResponseEntity(saved, HttpStatus.OK)
        } catch (e: Exception) {
            return ResponseEntity<String>(e.localizedMessage, HttpStatus.CONFLICT)
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @Modifying
    @PostMapping("/questionitem",produces = ["application/hal+json", "application/text"], consumes = ["application/hal+json","application/json"])
    fun postQuestionItem(@RequestBody questionItem: QuestionItem): ResponseEntity<*> {

        try {
            if (questionItem.responseName.isBlank() &&(questionItem.responseId != null) && (this.responseDomainRepository != null)) {
                val response =loadRevisionEntity(questionItem.responseId!!, repository = this.responseDomainRepository)
                questionItem.responseName = response.name
            }

            val saved = repository.save(questionItem)

            return ResponseEntity<QuestionItem>(saved, HttpStatus.CREATED)
        } catch (e: Exception) {
            return ResponseEntity<String>(e.localizedMessage, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    override fun entityModelBuilder(entity: QuestionItem): RepresentationModel<EntityModel<QuestionItem>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"questionitem")
        logger.debug("ModelBuilder QuestionItem : {}", uriId)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)

        val response =
            if ((entity.responseId != null) && (this.responseDomainRepository != null) && (entity.response == null)) {
                loadRevisionEntity(entity.responseId!!, repository = this.responseDomainRepository)
            } else {
                entity.response
            }

        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(response?.let { entityModelBuilder(it) }?:"",LinkRelation.of("responseDomain") )
            .build()
    }

    fun entityModelBuilder(entity: ResponseDomain): RepresentationModel<EntityModel<ResponseDomain>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"responsedomain")
        logger.debug("ModelBuilder QuestionItem:ResponseDomain : {}", uriId)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        Hibernate.initialize(entity.managedRepresentation)

        repLoaderService.getRepository<Category>(ElementKind.CATEGORY).let { rr ->
            entity.managedRepresentation.children =
                EntityAuditTrailListener.loadChildrenDefault(entity.managedRepresentation, rr)
        }

        var _index = 0
        EntityAuditTrailListener.populateCatCodes(entity.managedRepresentation, _index, entity.codes)

        val user =
            this.factory?.createProjection(UserListe::class.java, entity.modifiedBy)
        val managedRepresentation =
            this.factory?.createProjection(ManagedRepresentation::class.java, entity.managedRepresentation)

        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(user!!, LinkRelation.of("modifiedBy"))
            .embed(managedRepresentation!!, LinkRelation.of("managedRepresentation"))
            .build()
    }
}
