package no.nsd.qddt.controller

import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.QuestionItemRepository
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
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
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
    @GetMapping("/questionitem/revisions/{uri}", produces = ["application/hal+json;charset=UTF-8"])
    @ResponseBody
    fun getRevisions(
        @PathVariable uri: UUID,
        pageable: Pageable
    ): RepresentationModel<*> {
        return super.getRevisions(uri, pageable, QuestionItem::class.java)
    }


//    @Transactional(propagation = Propagation.REQUIRED)
//    @ResponseBody
//    @GetMapping("/questionitem/{uri}", produces = ["application/hal+json;charset=UTF-8"])
//    fun getById(@PathVariable uri: UUID): RepresentationModel<*> {
//        return entityModelBuilder(repository.getById(uri))
//    }

    @GetMapping("/questionitem/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/questionitem/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): String {
        return super.getXml(uri)
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
