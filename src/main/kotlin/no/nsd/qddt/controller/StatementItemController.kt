package no.nsd.qddt.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import no.nsd.qddt.config.exception.FileUploadException
import no.nsd.qddt.model.StatementItem
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.repository.ControlConstructRepository
import no.nsd.qddt.service.OtherMaterialService
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*


@BasePathAwareController
class StatementItemController(@Autowired repository: ControlConstructRepository<StatementItem>) :
    AbstractRestController<StatementItem>(repository) {

    @Autowired
    lateinit var omService: OtherMaterialService

    @Transactional(propagation = Propagation.REQUIRED)
    @ResponseBody
    @GetMapping("/statementitem/{uuid}", produces = ["application/hal+json"])
    fun getById(@PathVariable uuid: UUID): RepresentationModel<*> {
        return entityModelBuilder(repository.getById(uuid) as StatementItem)
    }
    @ResponseBody
    @Modifying
    @PostMapping(value = ["/statementitem"])
    fun update(@RequestBody instance: StatementItem): StatementItem {
        return repository.save(instance)
    }

    @ResponseBody
    @Modifying
    @PostMapping(value = ["/statementitem/createfile"], headers = ["content-type=multipart/form-data"])
    @Throws(FileUploadException::class, IOException::class)
    fun createWithFile(
        @RequestParam("files") files: Array<MultipartFile>?,
        @RequestParam("controlconstruct") jsonString: String
    ): RepresentationModel<*> {
        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val index = jsonString.indexOf("\"classKind\":\"QUESTION_CONSTRUCT\"")

        val instance = mapper.readValue(jsonString, StatementItem::class.java)

        if (files != null && files.isNotEmpty()) {
            logger.info("got new files!!!")

            if (null == instance.id) instance.id = UUID.randomUUID()

            for (multipartFile in files) {
                instance.otherMaterials.add(omService.saveFile(multipartFile, instance.id!!))
            }
            if (IBasedOn.ChangeKind.CREATED == instance.changeKind) instance.changeKind =
                IBasedOn.ChangeKind.TO_BE_DELETED
        }
        return   entityModelBuilder(repository.save(instance))
    }


     override fun entityModelBuilder(entity: StatementItem): RepresentationModel<*> {
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        logger.debug("entityModelBuilder QuestionConstruct : {}", uriId)
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
            .build()
    }


}