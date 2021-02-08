//package no.nsd.qddt.domain.concept.web
//
//import no.nsd.qddt.domain.AbstractController
//import no.nsd.qddt.domain.concept.ConceptService
//import org.springframework.data.domain.Pageable
//import org.springframework.http.MediaType
//import org.springframework.web.bind.annotation.ResponseStatus
//import java.lang.Exception
//
///**
// * @author Stig Norland
// * @author Dag Østgulen Heradstveit
// */
//@RestController
//@RequestMapping("/concept")
//class ConceptController @Autowired constructor(conceptService: ConceptService, topicGroupService: TopicGroupService) :
//    AbstractController() {
//    private val service: ConceptService
//    private val topicGroupService: TopicGroupService
//
//    @ResponseStatus(value = HttpStatus.OK)
//    @RequestMapping(value = ["{id}"], method = [RequestMethod.GET])
//    operator fun get(@PathVariable("id") id: UUID?): ConceptJsonEdit {
//        return concept2Json(service.findOne(id))
//    }
//
//    @ResponseStatus(value = HttpStatus.OK)
//    @RequestMapping(value = [""], method = [RequestMethod.POST])
//    fun update(@RequestBody concept: Concept): ConceptJsonEdit {
//        LOG.debug(concept.toString())
//        return concept2Json(service.save(concept))
//    }
//
//    @ResponseStatus(value = HttpStatus.CREATED)
//    @RequestMapping(
//        value = ["/combine"],
//        method = [RequestMethod.POST],
//        params = ["parentId", "questionitemid", "questionitemrevision"]
//    )
//    fun addQuestionItem(
//        @RequestParam("parentId") conceptId: UUID?,
//        @RequestParam("questionitemid") questionItemId: UUID?,
//        @RequestParam("questionitemrevision") questionItemRevision: Number?
//    ): ConceptJsonEdit {
//        var questionItemRevision = questionItemRevision
//        return try {
//            val concept: Concept = service.findOne(conceptId)
//            if (questionItemRevision == null) questionItemRevision = 0
//            concept.addQuestionItem(questionItemId, questionItemRevision.toInt())
//            concept2Json(service.save(concept))
//        } catch (ex: Exception) {
//            LOG.error("addQuestionItem", ex)
//            throw ex
//        }
//    }
//
//    @ResponseStatus(value = HttpStatus.OK)
//    @RequestMapping(value = ["/decombine"], method = [RequestMethod.POST], params = ["parentId", "questionitemid"])
//    fun removeQuestionItem(
//        @RequestParam("parentId") conceptId: UUID?,
//        @RequestParam("questionitemid") questionItemId: UUID?,
//        @RequestParam("questionitemrevision") questionItemRevision: Number
//    ): ConceptJsonEdit {
//        var concept: Concept? = null
//        return try {
//            concept = service.findOne(conceptId)
//            concept.removeQuestionItem(questionItemId, questionItemRevision.toInt())
//            concept2Json(service.save(concept))
//        } catch (ex: Exception) {
//            LOG.error("removeQuestionItem", ex)
//            concept2Json(concept)
//        }
//    }
//
//    @ResponseStatus(value = HttpStatus.CREATED)
//    @RequestMapping(value = ["/copy/{uuid}/{rev}/{parentUuid}"], method = [RequestMethod.POST])
//    fun copy(
//        @PathVariable("uuid") sourceId: UUID?,
//        @PathVariable("rev") sourceRev: Int?,
//        @PathVariable("parentUuid") parentId: UUID?
//    ): ConceptJsonEdit {
//        return concept2Json(
//            service.save(
//                service.copy(sourceId, sourceRev, parentId)
//            )
//        )
//    }
//
//    @ResponseStatus(value = HttpStatus.CREATED)
//    @RequestMapping(value = ["/create/{uuid}"], method = [RequestMethod.POST])
//    fun createByParent(@RequestBody concept: Concept?, @PathVariable("uuid") parentId: UUID?): ConceptJsonEdit {
//        return if (service.exists(parentId)) {
//            concept2Json(
//                service.save(
//                    service
//                        .findOne(parentId)
//                        .addChildren(concept)
//                )
//            )
//        } else {
//            concept2Json(
//                service.save(
//                    topicGroupService
//                        .findOne(parentId)
//                        .addConcept(concept)
//                )
//            )
//        }
//    }
//
//    @ResponseStatus(value = HttpStatus.OK)
//    @RequestMapping(value = ["/delete/{id}"], method = [RequestMethod.DELETE])
//    fun delete(@PathVariable("id") id: UUID?) {
//        service.delete(id)
//    }
//
//    @RequestMapping(value = ["/page"], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun getAll(
//        pageable: Pageable?,
//        assembler: PagedResourcesAssembler<ConceptJsonEdit?>
//    ): PagedModel<EntityModel<ConceptJsonEdit>> {
//        return assembler.toModel(
//            service.findAllPageable(pageable).map { ConceptJsonEdit() }
//        )
//    }
//
//    @RequestMapping(
//        value = ["/page/by-parent/{topicId}"],
//        method = [RequestMethod.GET],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    fun getbyPageTopicId(
//        @PathVariable("topicId") id: UUID?,
//        pageable: Pageable?,
//        assembler: PagedResourcesAssembler<ConceptJsonEdit?>
//    ): PagedModel<EntityModel<ConceptJsonEdit>> {
//        return assembler.toModel(
//            service.findByTopicGroupPageable(id, pageable).map { ConceptJsonEdit() }
//        )
//    }
//
//    @RequestMapping(
//        value = ["/list/by-parent/{topicId}"],
//        method = [RequestMethod.GET],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    fun getbyTopicId(@PathVariable("topicId") id: UUID?): List<ConceptJsonEdit> {
//        return service.findByTopicGroup(id).stream().map { ConceptJsonEdit() }.collect(Collectors.toList())
//    }
//
//    @RequestMapping(
//        value = ["/page/search"],
//        method = [RequestMethod.GET],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    fun getBy(
//        @RequestParam(value = "name", defaultValue = "%") name: String?,
//        @RequestParam(value = "description", defaultValue = "%") description: String?,
//        pageable: Pageable?,
//        assembler: PagedResourcesAssembler<ConceptJsonEdit?>
//    ): PagedModel<EntityModel<ConceptJsonEdit>> {
//        return assembler.toModel(
//            service.findByNameAndDescriptionPageable(name, description, pageable).map { ConceptJsonEdit() }
//        )
//    }
//
//    @ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED)
//    @RequestMapping(
//        value = ["/list/by-QuestionItem/{qiId}"],
//        method = [RequestMethod.GET],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    fun getByQuestionItemId(@PathVariable("qiId") id: UUID?): List<Concept> {
//        return service.findByQuestionItem(id, null)
//    }
//
//    @ResponseStatus(value = HttpStatus.OK)
//    @RequestMapping(value = ["/xml/{id}"], method = [RequestMethod.GET])
//    fun getXml(@PathVariable("id") id: UUID?): String {
//        return XmlDDIFragmentAssembler<Concept>(service.findOne(id)).compileToXml()
//    }
//
//    @ResponseBody
//    @RequestMapping(value = ["/pdf/{id}"], method = [RequestMethod.GET], produces = ["application/pdf"])
//    fun getPdf(@PathVariable("id") id: UUID?): ByteArray {
//        return service.findOne(id).makePdf().toByteArray()
//    }
//
//    private fun concept2Json(concept: Concept?): ConceptJsonEdit {
//        return ConceptJsonEdit(concept)
//    }
//
//    init {
//        service = conceptService
//        this.topicGroupService = topicGroupService
//    }
//}
