package no.nsd.qddt.controller

import no.nsd.qddt.model.Instrument
import no.nsd.qddt.model.User
import no.nsd.qddt.repository.InstrumentRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayInputStream
import java.util.*

@Transactional(propagation = Propagation.REQUIRED)
@BasePathAwareController
class InstrumentController(@Autowired repository: InstrumentRepository) :
    AbstractRestController<Instrument>(repository) {

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/instrument/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/instrument/revisions/{uuid}", produces = ["application/hal+json"])
    override fun getRevisions(
        @PathVariable uuid: UUID,
        pageable: Pageable
    ): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
    }


    @GetMapping("/instrument/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/instrument/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }
//    @ResponseBody
//    @GetMapping("/publication/{uri}", produces = ["application/hal+json"])
//    fun get(@PathVariable uri: UUID):RepresentationModel<*> {
//        return entityModelBuilder(repository.getById(uri))
//    }

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @PostMapping("/instrument", produces = ["application/hal+json"])
    fun insert(@RequestBody instrument: Instrument): ResponseEntity<*> {
        return try {
            val saved = repository.saveAndFlush(instrument)
            ResponseEntity(saved, HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity<String>(e.localizedMessage, HttpStatus.CONFLICT)
        }
//        return entityModelBuilder(repository.saveAndFlush(instrument))
    }

    @ResponseBody
    @PutMapping("/instrument/{uuid}", produces = ["application/hal+json"])
    fun update(@PathVariable uuid: UUID,@RequestBody instrument: Instrument): ResponseEntity<*> {
        return try {
            val saved = repository.saveAndFlush(instrument)
            if (saved.agency == null) {
                val currentuser = SecurityContextHolder.getContext().authentication.principal as User
                saved.modifiedBy = currentuser
                saved.agency = currentuser.agency
            }
            ResponseEntity(saved, HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity<String>(e.localizedMessage, HttpStatus.CONFLICT)
        }
//        val result = repository.saveAndFlush(instrument)
//        if (result.agency == null) {
//            val currentuser = SecurityContextHolder.getContext().authentication.principal as User
//            result.modifiedBy = currentuser
//            result.agency = currentuser.agency
//        }
//        return entityModelBuilder(result)
    }


//    @ResponseBody
//    @GetMapping("/instrument/search/findByQuery", produces = ["application/hal+json"])
//    fun getByQuery(publicationCriteria: PublicationCriteria, pageable: Pageable?): RepresentationModel<*> {
//
//        logger.debug(publicationCriteria.toString())
//        val entities = (repository as InstrumentRepository).findByQuery(
//            publicationCriteria.publishedKind!!,
//            publicationCriteria.publicationStatus!!,
//            publicationCriteria.purpose!!,
//            publicationCriteria.xmlLang!!,
//            publicationCriteria.name!!,
//            publicationCriteria.getAngencyId(), pageable
//        ).map {
//            entityModelBuilder(it)
//        }
//
//        return PagedModel.of(entities.content, pageMetadataBuilder(entities), Link.of("publications"))
//    }

    override fun entityModelBuilder(entity: Instrument): RepresentationModel<EntityModel<Instrument>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"publication")
        logger.debug("EntModBuild Instrument : {}", uriId)

        entity.comments.size
        entity.comments.forEach {
            logger.debug("initialize(comments.modifiedBy)")
            Hibernate.initialize(it.modifiedBy)
        }
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .build()
    }

}
