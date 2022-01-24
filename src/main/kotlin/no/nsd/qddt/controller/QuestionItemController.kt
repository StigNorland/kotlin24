package no.nsd.qddt.controller

import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.RepLoaderService
import no.nsd.qddt.repository.QuestionItemRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.data.rest.webmvc.RepositoryRestController
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
import java.util.*
import javax.persistence.EntityManager

@BasePathAwareController
class QuestionItemController(@Autowired repository: QuestionItemRepository): AbstractRestController<QuestionItem>(repository) {

//    @GetMapping("/questionitem/{uri}",produces = ["application/hal+json"])
//    override fun getById(@PathVariable uri: String): ResponseEntity<EntityModel<QuestionItem>> {
//        return super.getById(uri)
//    }

    @Autowired
    private val applicationContext: ApplicationContext? = null

//    private val repLoaderService =  applicationContext?.getBean("repLoaderService") as RepLoaderService

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/questionitem/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String):RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/questionitem/revisions/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: UUID, pageable: Pageable):RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }


    @GetMapping("/questionitem/{uri}/pdf", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/questionitem/{uri}/xml", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return  super.getXml(uri)
    }


    override fun entityModelBuilder(entity: QuestionItem): RepresentationModel<EntityModel<QuestionItem>> {
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        logger.debug("entityModelBuilder QuestionItem : {}" , uriId)
        val baseUrl = if(uriId.rev != null)
            "${baseUri}/questionitem/revision/${uriId}"
        else
            "${baseUri}/questionitem/${uriId.id}"
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)

        val response =
            when {
                entity.responseId?.rev != null && entity.responseDomain == null -> {
                    val repository =
                        (applicationContext?.getBean("repLoaderService") as RepLoaderService)
                            .getRepository<ResponseDomain>(ElementKind.RESPONSEDOMAIN)

                    loadRevisionEntity(entity.responseId!!,repository)
                }
                else -> {}
            }

        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(response as ResponseDomain,LinkRelation.of("responseDomain") )
            .build()
    }


    private fun <T: AbstractEntityAudit>loadRevisionEntity(uri: UriId, repository: RevisionRepository<T, UUID, Int>): T {
        return with(uri) {
            if (rev != null)
                repository.findRevision(id,rev!!).map {
                    it.entity.version.rev = it.revisionNumber.get()
                    it.entity
                }.get()
            else
                repository.findLastChangeRevision(id).map {
                    it.entity.version.rev = it.revisionNumber.get()
                    it.entity
                }.get()
        }
    }
}
