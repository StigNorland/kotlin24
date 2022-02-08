package no.nsd.qddt.controller

import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.QuestionItemRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*


@BasePathAwareController
class QuestionItemController(@Autowired repository: QuestionItemRepository) :
    AbstractRestController<QuestionItem>(repository) {

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/questionitem/revision/{uri}", produces = ["application/hal+json;charset=UTF-8"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/questionitem/revisions/{uuid}", produces = ["application/hal+json;charset=UTF-8"])
    @ResponseBody
    override fun getRevisions(
        @PathVariable uuid: UUID,
        pageable: Pageable
    ): PagedModel<RepresentationModel<EntityModel<QuestionItem>>> {
        return super.getRevisions(uuid, pageable)
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @ResponseBody
    @GetMapping("/questionitem/{uuid}", produces = ["application/hal+json"])
    fun getById(@PathVariable uuid: UUID): RepresentationModel<*> {
        return entityModelBuilder(repository.getById(uuid))
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/questionitem/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/questionitem/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): String {
        return super.getXml(uri)
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @ResponseBody
    @Modifying
    @PutMapping("/questionitem/{uuid}",produces = ["application/hal+json", "application/text"], consumes = ["application/hal+json","application/json"])
    open fun putQuestionItem(@PathVariable uuid: UUID, @RequestBody questionItem: QuestionItem): ResponseEntity<*> {

        try {

            val saved = repository.save(questionItem)

            return ResponseEntity<QuestionItem>( HttpStatus.OK)
        } catch (e: Exception) {
            return ResponseEntity<String>(e.localizedMessage, HttpStatus.CONFLICT)
        }
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @ResponseBody
    @Modifying
    @PostMapping("/questionitem",produces = ["application/hal+json", "application/text"], consumes = ["application/hal+json","application/json"])
    open fun postQuestionItem(@RequestBody questionItem: QuestionItem): ResponseEntity<*> {

        try {

            val saved = repository.save(questionItem)

            return ResponseEntity<QuestionItem>(saved, HttpStatus.CREATED)
        } catch (e: Exception) {
            return ResponseEntity<String>(e.localizedMessage, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    override fun entityModelBuilder(entity: QuestionItem): RepresentationModel<EntityModel<QuestionItem>> {
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        logger.debug("entityModelBuilder QuestionItem : {}", uriId)
        val baseUrl = if (uriId.rev != null)
            "${baseUri}/questionitem/revision/${uriId}"
        else
            "${baseUri}/questionitem/${uriId.id}"
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)


        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
//            .embed(entity.responseDomain!!,LinkRelation.of("responseDomain") )
            .build()
    }

}
