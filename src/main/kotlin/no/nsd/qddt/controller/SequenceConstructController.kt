package no.nsd.qddt.controller

import no.nsd.qddt.model.Sequence
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.ControlConstructRepository
import no.nsd.qddt.service.OtherMaterialService
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*

@Transactional(propagation = Propagation.REQUIRED)
@BasePathAwareController
class SequenceConstructController(@Autowired repository: ControlConstructRepository<Sequence>) :
    AbstractRestController<Sequence>(repository) {

    @Autowired
    lateinit var omService: OtherMaterialService


    @GetMapping("/sequence/revision/{uri}", produces = ["application/hal+json"])
    @ResponseBody
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @GetMapping("/sequence/revisions/{uuid}", produces = ["application/hal+json"])
    @ResponseBody
    override fun getRevisions(@PathVariable uuid: UUID,pageable: Pageable): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
    }


    @ResponseBody
    @GetMapping("/sequence/{uuid}", produces = ["application/hal+json"])
    fun getById(@PathVariable uuid: UUID): RepresentationModel<*> {
        return entityModelBuilder(repository.getById(uuid))
    }

    @ResponseBody
    @Modifying
    @PostMapping(value = ["/sequence"])
    fun update(@RequestBody instance: Sequence): Sequence {
        return repository.save(instance)
    }


    override fun entityModelBuilder(entity: Sequence): RepresentationModel<*> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"sequence")
        logger.debug("entityModelBuilder Sequence : {}", uriId)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        Hibernate.initialize(entity.condition)
        entity.otherMaterials.size
        entity.sequence.size
        entity.universe.size

        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .build()
    }


}
