package no.nsd.qddt.controller

import no.nsd.qddt.model.ConditionConstruct
import no.nsd.qddt.repository.ControlConstructRepository
import no.nsd.qddt.repository.handler.EntityAuditTrailListener
import no.nsd.qddt.service.OtherMaterialService
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
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
class ConditionConstructController(@Autowired repository: ControlConstructRepository<ConditionConstruct>) :
    AbstractRestController<ConditionConstruct>(repository) {

    @Autowired
    lateinit var omService: OtherMaterialService

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/conditionconstruct/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/conditionconstruct/revisions/{uuid}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uuid: UUID, pageable: Pageable): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
    }


    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/conditionconstruct/{uuid}", produces = ["application/hal+json"])
    fun getById(@PathVariable uuid: UUID): RepresentationModel<*> {
        return entityModelBuilder(repository.getById(uuid))
    }

    @ResponseBody
    @PutMapping(value = ["/conditionconstruct/{uuid}"], produces = ["application/hal+json"])
    fun update(@PathVariable uuid: UUID, @RequestBody instance: ConditionConstruct): ConditionConstruct {
        return repository.saveAndFlush(instance)
    }

    @ResponseBody
    @PostMapping(value = ["/conditionconstruct"], produces = ["application/hal+json"])
    fun update(@RequestBody instance: ConditionConstruct): ConditionConstruct {
        return repository.save(instance)
    }

    override fun entityModelBuilder(entity: ConditionConstruct): RepresentationModel<*> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId, "conditionconstruct")
        logger.debug("ModelBuilder Condition : {}", uriId)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        entity.otherMaterials.size
        entity.sequence.size

        entity.parameterIn = EntityAuditTrailListener.getParameterIn(entity)
        entity.parameterOut = EntityAuditTrailListener.getParameterOut(entity)

        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .build()
    }
}
