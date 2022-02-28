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

    @ResponseBody
    @GetMapping("/sequence/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @ResponseBody
    @GetMapping("/sequence/revisions/{uuid}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uuid: UUID,pageable: Pageable): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
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

        val baseUrl = baseUrl( toUriId(entity),"sequence")
        logger.debug("entityModelBuilder Sequence : {}", baseUrl)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        Hibernate.initialize(entity.condition)
        entity.otherMaterials.size
        entity.sequence.size
        entity.universe.size

        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .build()
    }

}
