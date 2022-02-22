package no.nsd.qddt.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import no.nsd.qddt.config.exception.FileUploadException
import no.nsd.qddt.model.*
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.repository.ControlConstructRepository
import no.nsd.qddt.repository.handler.EntityAuditTrailListener
import no.nsd.qddt.service.OtherMaterialService
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*


@BasePathAwareController
class ConditionConstructController(@Autowired repository: ControlConstructRepository<ConditionConstruct>) :
    AbstractRestController<ConditionConstruct>(repository) {

    @Autowired
    lateinit var omService: OtherMaterialService

    @Transactional(propagation = Propagation.REQUIRED)
    @ResponseBody
    @GetMapping("/conditionconstruct/{uuid}", produces = ["application/hal+json"])
    fun getById(@PathVariable uuid: UUID): RepresentationModel<*> {
        return entityModelBuilder(repository.getById(uuid))
    }

    @ResponseBody
    @Modifying
    @PostMapping(value = ["/conditionconstruct"])
    fun update(@RequestBody instance: ConditionConstruct): ConditionConstruct {
        return repository.save(instance)
    }


     override fun entityModelBuilder(entity: ConditionConstruct): RepresentationModel<*> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"conditionconstruct")
        logger.debug("entityModelBuilder ConditionConstruct : {}", uriId)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)


        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .build()
    }




}