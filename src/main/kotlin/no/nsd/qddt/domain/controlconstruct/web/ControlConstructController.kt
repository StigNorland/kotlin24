package no.nsd.qddt.domain.controlconstruct.web

import no.nsd.qddt.domain.AbstractController
import no.nsd.qddt.domain.controlconstruct.pojo.Sequence
import no.nsd.qddt.utils.StringTool.likeify
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ResponseStatus
import java.io.IOException

/**
 * This controller relates to a meta storage, which has a rank,logic and Rankrationale property, and thus need control
 *
 * @author Stig Norland
 */
@RestController
@RequestMapping("/controlconstruct")
class ControlConstructController @Autowired constructor(
    ccService: ControlConstructService,
    otherMaterialService: OtherMaterialService
) : AbstractController() {
    private val service: ControlConstructService
    private val omService: OtherMaterialService

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = ["{id}"], method = [RequestMethod.GET])
    operator fun <S : ControlConstruct?> get(@PathVariable("id") id: UUID?): S {
        return service.findOne(id)
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = ["/condition"], method = [RequestMethod.POST])
    fun update(@RequestBody instance: ConditionConstruct?): ConditionConstruct {
        return service.save(instance)
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = ["/question"], method = [RequestMethod.POST])
    fun update(@RequestBody instance: QuestionConstruct?): QuestionConstruct {
        return service.save(instance)
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = ["/sequence"], method = [RequestMethod.POST])
    fun update(@RequestBody instance: Sequence?): Sequence {
        return service.save(instance)
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = ["/statement"], method = [RequestMethod.POST])
    fun update(@RequestBody instance: StatementItem?): StatementItem {
        return service.save(instance)
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(
        value = ["/createfile"],
        method = [RequestMethod.POST],
        headers = ["content-type=multipart/form-data"]
    )
    @Throws(
        IOException::class
    )
    fun createWithFile(
        @RequestParam("files") files: Array<MultipartFile?>?,
        @RequestParam("controlconstruct") jsonString: String
    ): ControlConstruct {
        val mapper: ObjectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val index = jsonString.indexOf("\"classKind\":\"QUESTION_CONSTRUCT\"")
        val instance: ControlConstruct
        instance = if (index > 0) {
            mapper.readValue(jsonString, QuestionConstruct::class.java)
        } else {
            mapper.readValue(jsonString, Sequence::class.java)
        }
        if (files != null && files.size > 0) {
            LOG.info("got new files!!!")
            if (null == instance.id) {
                instance.id = UUID.randomUUID()
            }
            for (multipartFile in files) {
                instance.getOtherMaterials().add(omService.saveFile(multipartFile, instance.id))
            }
            if (ChangeKind.CREATED == instance.changeKind) instance.changeKind = null
        }
        return service.save(instance)
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = ["/delete/{id}"], method = [RequestMethod.DELETE])
    fun delete(@PathVariable("id") id: UUID?) {
        service.delete(id)
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = ["/list/by-question/{uuid}"], method = [RequestMethod.GET])
    fun getBySecond(@PathVariable("uuid") secondId: UUID): List<ConstructQuestionJson> {
        return try {
            service.findByQuestionItems(listOf<UUID>(secondId))
        } catch (ex: Exception) {
            LOG.error("getBySecond", ex)
            throw ex
        }
    }

    @RequestMapping(
        value = ["/page/search"],
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getBy(
        @RequestParam(value = "name", defaultValue = "") name: String?,
        @RequestParam(value = "description", defaultValue = "") description: String?,
        @RequestParam(value = "questionText", defaultValue = "") questionText: String?,
        @RequestParam(value = "questionName", defaultValue = "") questionName: String?,
        @RequestParam(value = "constructKind", defaultValue = "QUESTION_CONSTRUCT") kind: String,
        @RequestParam(value = "sequenceKind", defaultValue = "") sequenceKind: String?,
        @RequestParam(value = "xmlLang", defaultValue = "") xmlLang: String?,
        pageable: Pageable?,
        assembler: PagedResourcesAssembler<ConstructJsonView?>
    ): PagedModel<EntityModel<ConstructJsonView>> {
        val controlConstructs: Page<ConstructJsonView>
        // Originally name and question was 2 separate search strings, now we search both name and questiontext for value in "question"
        // Change in frontEnd usage made it necessary to distinguish
        controlConstructs = if (kind == "QUESTION_CONSTRUCT") {
            service.findQCBySearch(
                likeify(name),
                likeify(questionName),
                likeify(questionText),
                likeify(xmlLang),
                pageable
            ) //.map( source -> Converter.mapConstruct( source ));
        } else {
            service.findBySearcAndControlConstructKind(
                kind,
                sequenceKind,
                likeify(name),
                likeify(description),
                likeify(xmlLang),
                pageable
            )
        }
        return assembler.toModel(controlConstructs)
    }

    @ResponseBody
    @RequestMapping(value = ["/pdf/{id}"], method = [RequestMethod.GET], produces = ["application/pdf"])
    fun getPdf(@PathVariable("id") id: UUID?): ByteArray {
        return service.findOne(id).makePdf().toByteArray()
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = ["/xml/{id}"], method = [RequestMethod.GET])
    fun getXml(@PathVariable("id") id: UUID?): String {
        return XmlDDIFragmentAssembler<ControlConstruct>(service.findOne(id)).compileToXml()
    }

    init {
        service = ccService
        omService = otherMaterialService
    }
}
