package no.nsd.qddt.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import no.nsd.qddt.config.exception.FileUploadException
import no.nsd.qddt.model.*
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.repository.*
import no.nsd.qddt.repository.handler.EntityAuditTrailListener
import no.nsd.qddt.repository.projection.ManagedRepresentation
import no.nsd.qddt.repository.projection.UserListe
import no.nsd.qddt.service.OtherMaterialService
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.projection.ProjectionFactory
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*

@Transactional(propagation = Propagation.REQUIRED)
@BasePathAwareController
class QuestionConstructController(@Autowired repository: QuestionConstructRepository) :
    AbstractRestController<QuestionConstruct>(repository) {

    @Autowired
    private val questionItemRepository: QuestionItemRepository? = null

    @Autowired
    private val responseDomainRepository: ResponseDomainRepository? = null


    @Autowired
    private val factory: ProjectionFactory? = null


    @Autowired
    lateinit var omService: OtherMaterialService


    @GetMapping("/questionconstruct/revision/{uri}", produces = ["application/hal+json"])
    @ResponseBody
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.NESTED)
    @GetMapping("/questionconstruct/revisions/{uuid}", produces = ["application/hal+json"])
    @ResponseBody
    override fun getRevisions(@PathVariable uuid: UUID,pageable: Pageable): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
    }

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/questionconstruct/{uri}", produces = ["application/hal+json"])
    fun getQuestionConstruct(@PathVariable uri: UUID):  RepresentationModel<*> {
        return entityModelBuilder(repository.findById(uri).orElseThrow())
    }

    @GetMapping("/questionconstruct/pdf/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/questionconstruct/xml/{uri}")
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

    @ResponseBody
    @PostMapping(value = ["/questionconstruct"])
    fun update(@RequestBody instance: QuestionConstruct): QuestionConstruct {
        return repository.save(instance)
    }



//    @ResponseBody
//    @GetMapping("/questionconstruct/search/findByQuery", produces = ["application/hal+json"])
//    fun getByQuery(ccc: ControlConstructCriteria, pageable: Pageable): RepresentationModel<*> {
//        logger.debug(ccc.toString())
//        val qPage: Pageable = if (pageable.sort.isUnsorted) {
//            PageRequest.of(pageable.pageNumber, pageable.pageSize*2, Sort.Direction.DESC, "modified")
//        } else {
//            pageable
//        }
//
//        val repro = repository as ControlConstructRepository<ControlConstruct>
//
//        val entities = repro.findByQuery<QuestionConstruct>(
//            constructKind= ccc.constructKind,
//            xmlLang = ccc.xmlLang,
//            superKind = ccc.superKind,
//            label = ccc.label,
//            name = ccc.name,
//            description = ccc.description,
//            questionName = ccc.questionName,
//            questionText = ccc.questionText,
//            pageable = qPage
//        ).map {
//            entityModelBuilder(it)  as RepresentationModel<EntityModel<QuestionConstruct>>
//        }
//
//        return PagedModel.of(entities.content, pageMetadataBuilder(entities), Link.of("questionConstructs"))
//    }

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @PostMapping(value = ["/questionconstruct/createfile"], headers = ["content-type=multipart/form-data"], produces = ["application/hal+json"])
    @Throws(FileUploadException::class, IOException::class)
    fun createWithFile(
        @RequestParam("files") files: Array<MultipartFile>?,
        @RequestParam("controlconstruct") jsonString: String
    ): RepresentationModel<*> {
        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//        val index = jsonString.indexOf("\"classKind\":\"QUESTION_CONSTRUCT\"")

        val instance =  mapper.readValue(jsonString, QuestionConstruct::class.java)
        val currentuser = SecurityContextHolder.getContext().authentication.principal as User
        instance.modifiedBy = currentuser
        instance.agency = currentuser.agency

        if (files != null && files.isNotEmpty()) {
            logger.info("got new files!!!")

            if (null == instance.id) instance.id = UUID.randomUUID()

            for (multipartFile in files) {
                var om = omService.saveFile(multipartFile, instance.id!!)
                instance.otherMaterials.add(om)
            }
            if (IBasedOn.ChangeKind.CREATED == instance.changeKind)
                instance.changeKind = IBasedOn.ChangeKind.TO_BE_DELETED
        }
        val saved = repository.saveAndFlush(instance)
        return   entityModelBuilder(saved)
    }


    override fun entityModelBuilder(entity: QuestionConstruct): RepresentationModel<*> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"questionconstruct")
        logger.info("EntModBuild QC : {}", uriId)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        entity.otherMaterials.size
        entity.controlConstructInstructions.size
//        entity.preInstructions.size

         val question =
             if ((entity.questionId != null) && (this.questionItemRepository != null) && (entity.questionItem == null)) {
                 loadRevisionEntity(entity.questionId!!, repository = this.questionItemRepository)
             } else {
                 logger.debug("qi auto fetched")
                 entity.questionItem
             }

        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(question?.let { entityModelBuilder(it) }?:"",LinkRelation.of("questionItem"))
            .build()
    }


    fun entityModelBuilder(entity: QuestionItem): RepresentationModel<EntityModel<QuestionItem>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"questionitem")
        logger.info("EntModBuild QC:QI : {}", uriId)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)

        val response =
            if ((entity.responseId != null) && (this.responseDomainRepository != null) && (entity.response == null)) {
                loadRevisionEntity(entity.responseId!!, repository = this.responseDomainRepository)
            } else {
                entity.response
            }
        var _index = 0
        if (response?.managedRepresentation != null) {
            EntityAuditTrailListener.populateCatCodes(response.managedRepresentation, _index, response.codes)
        }

        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(response?.let { entityModelBuilder(it) }?:"",LinkRelation.of("responseDomain") )
            .build()
    }

    fun entityModelBuilder(entity: ResponseDomain): RepresentationModel<EntityModel<ResponseDomain>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"responsedomain")
        logger.info("EntModBuild QC:Response : {}", uriId)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        Hibernate.initialize(entity.managedRepresentation)

        entity.managedRepresentation?.children?.forEach {
            if (it.hierarchyLevel == HierarchyLevel.GROUP_ENTITY)
                it.children.size
        }
        val user =
            this.factory?.createProjection(UserListe::class.java, entity.modifiedBy)
        val managedRepresentation =
            this.factory?.createProjection(ManagedRepresentation::class.java, entity.managedRepresentation!!)

        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(user!!, LinkRelation.of("modifiedBy"))
            .embed(managedRepresentation!!, LinkRelation.of("managedRepresentation"))
            .build()
    }

}
