package no.nsd.qddt.controller

import no.nsd.qddt.model.Publication
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.PublicationRepository
import no.nsd.qddt.repository.criteria.PublicationCriteria
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.MediaType
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*

@Transactional(propagation = Propagation.REQUIRED)
@BasePathAwareController
class PublicationController(@Autowired repository: PublicationRepository) :
    AbstractRestController<Publication>(repository) {

    @GetMapping("/publication/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @GetMapping("/publication/revisions/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(
        @PathVariable uri: UUID,
        pageable: Pageable
    ): PagedModel<RepresentationModel<EntityModel<Publication>>> {
        return super.getRevisions(uri, pageable)
    }


    @GetMapping("/publication/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/publication/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): String {
        return super.getXml(uri)
    }
//    @ResponseBody
//    @GetMapping("/publication/{uri}", produces = ["application/hal+json"])
//    fun get(@PathVariable uri: UUID):RepresentationModel<*> {
//        return entityModelBuilder(repository.getById(uri))
//    }

    @ResponseBody
    @Modifying
    @PostMapping("/publication", produces = ["application/hal+json"])
    fun save(@RequestBody publication: Publication): RepresentationModel<*> {
        return entityModelBuilder(repository.saveAndFlush(publication))
    }

    @ResponseBody
    @GetMapping("/publication/search/findByQuery", produces = ["application/hal+json"])
    fun getByQuery(publicationCriteria: PublicationCriteria, pageable: Pageable?): RepresentationModel<*> {

        logger.debug(publicationCriteria.toString())
        val entities = (repository as PublicationRepository).findByQuery(
            publicationCriteria.publishedKind!!,
            publicationCriteria.publicationStatus!!,
            publicationCriteria.purpose!!,
            publicationCriteria.xmlLang!!,
            publicationCriteria.name!!,
            publicationCriteria.getAngencyId(), pageable
        ).map {
            entityModelBuilder(it)
        }

        return PagedModel.of(entities.content, pageMetadataBuilder(entities), Link.of("publications"))
    }

    override fun entityModelBuilder(entity: Publication): RepresentationModel<EntityModel<Publication>> {
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        logger.debug("entityModelBuilder Publication : {}", uriId)
        val baseUrl = if (uriId.rev != null)
            "${baseUri}/publication/revision/${uriId}"
        else
            "${baseUri}/publication/${uriId.id}"
        entity.comments.size
        entity.comments.forEach {
            logger.debug("initialize(comments.modifiedBy)")
            Hibernate.initialize(it.modifiedBy)
        }
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        Hibernate.initialize(entity.publicationElements)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.status!!, LinkRelation.of("status"))
            .build()
    }

}
