package no.nsd.qddt.controller

import no.nsd.qddt.model.Sequence
import no.nsd.qddt.repository.ControlConstructRepository
import no.nsd.qddt.service.OtherMaterialService
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*

@Transactional(propagation = Propagation.NESTED)
@BasePathAwareController
class SequenceConstructController(@Autowired repository: ControlConstructRepository<Sequence>) :
    AbstractRestController<Sequence>(repository) {

    @Autowired
    lateinit var omService: OtherMaterialService

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/sequence/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/sequence/revisions/{uuid}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uuid: UUID,pageable: Pageable): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
    }

    @GetMapping("/sequence/pdf/{uri}", produces = ["application/pdf"])
    override fun getPdf(@PathVariable uri: String): ResponseEntity<ByteArray> {
        logger.debug("PDF : {}", uri)
        return super.getPdf(uri)
    }

    @GetMapping("/sequence/xml/{uri}")
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

    @ResponseBody
    @GetMapping("/sequence/{uuid}", produces = ["application/hal+json"])
    fun getById(@PathVariable uuid: UUID): RepresentationModel<*> {
        return entityModelBuilder(repository.getById(uuid))
    }

    @ResponseBody
    @PutMapping("/sequence/{uuid}", produces = ["application/hal+json"])
    fun update(@PathVariable uuid: UUID, @RequestBody instance: Sequence): ResponseEntity<*>  {
        return try {
            val saved = repository.saveAndFlush(instance)
            ResponseEntity(saved, HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity<String>(e.localizedMessage, HttpStatus.CONFLICT)
        }
    }

    @ResponseBody
    @PostMapping(value = ["/sequence"])
    fun insert(@RequestBody instance: Sequence): ResponseEntity<*>  {
        return try {
            val saved = repository.saveAndFlush(instance)
            ResponseEntity(saved, HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity<String>(e.localizedMessage, HttpStatus.CONFLICT)
        }
    }


    override fun entityModelBuilder(entity: Sequence): RepresentationModel<*> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId, "sequence")

        logger.debug("ModelBuilder Sequence : {}", baseUrl)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        Hibernate.initialize(entity.condition)
        entity.otherMaterials.size
        entity.sequence.size
        entity.universe.size

        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
//            .link(Link.of(baseUrl( toUriId(entity),"sequence/revisions"),"revisions"))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.universe, LinkRelation.of("universe"))
            .build()
    }

}
