//package no.nsd.qddt.domain.questionitem.web
//
//import no.nsd.qddt.classes.exception.StackTraceFilter
//import no.nsd.qddt.classes.xml.XmlDDIFragmentAssembler
//import no.nsd.qddt.domain.questionitem.QuestionItem
//import no.nsd.qddt.domain.questionitem.QuestionItemService
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.Pageable
//import org.springframework.data.web.PagedResourcesAssembler
//import org.springframework.hateoas.EntityModel
//import org.springframework.hateoas.PagedModel
//import org.springframework.http.HttpStatus
//import org.springframework.http.MediaType
//import org.springframework.web.bind.annotation.*
//import java.lang.Exception
//import java.util.*
//
///**
// * @author Stig Norland
// */
//@RestController
//@RequestMapping("/questionitem")
//internal class QuestionItemController @Autowired constructor(service: QuestionItemService) {
//    private val service: QuestionItemService
//
//    @ResponseStatus(value = HttpStatus.OK)
//    @RequestMapping(value = ["{id}"], method = [RequestMethod.GET])
//    operator fun get(@PathVariable("id") id: UUID?): QuestionItemJsonEdit {
//        return question2Json(service.findOne(id))
//    }
//
//    @ResponseStatus(value = HttpStatus.OK)
//    @RequestMapping(value = [""], method = [RequestMethod.POST])
//    fun update(@RequestBody instance: QuestionItem?): QuestionItemJsonEdit {
//        return question2Json(service.save(instance))
//    }
//
//    @ResponseStatus(value = HttpStatus.CREATED)
//    @RequestMapping(value = ["/create"], method = [RequestMethod.POST])
//    fun create(@RequestBody instance: QuestionItem?): QuestionItemJsonEdit {
//        return question2Json(service.save(instance))
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
//        assembler: PagedResourcesAssembler<QuestionItemListJson?>
//    ): PagedModel<EntityModel<QuestionItemListJson>> {
//        val questionitems: Page<QuestionItemListJson> = service.findAllPageable(pageable).map { QuestionItemListJson() }
//        return assembler.toModel(questionitems)
//    }
//
//    @RequestMapping(
//        value = ["/page/search"],
//        method = [RequestMethod.GET],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    fun getBy(
//        @RequestParam(value = "name", defaultValue = "") name: String?,
//        @RequestParam(value = "question", defaultValue = "") question: String?,
//        @RequestParam(value = "responseDomainName", defaultValue = "") responseName: String?,
//        @RequestParam(value = "xmlLang", defaultValue = "") xmlLang: String?,
//        pageable: Pageable?,
//        assembler: PagedResourcesAssembler<QuestionItemListJson?>
//    ): PagedModel<EntityModel<QuestionItemListJson>> {
//        // Originally name and question was 2 separate search strings, now we search both name and questiontext for value in "question"
//        return try {
//            val questionitems: Page<QuestionItemListJson> =
//                service.findByNameOrQuestionOrResponseName(name, question, responseName, xmlLang, pageable)
//                    .map { QuestionItemListJson() }
//            assembler.toModel(questionitems)
//        } catch (ex: Exception) {
//            StackTraceFilter.println(ex.stackTrace)
//            throw ex
//        }
//    }
//
//    private fun question2Json(questionItem: QuestionItem): QuestionItemJsonEdit {
//        return QuestionItemJsonEdit(questionItem)
//    }
//
//    @ResponseBody
//    @RequestMapping(value = ["/pdf/{id}"], method = [RequestMethod.GET], produces = ["application/pdf"])
//    fun getPdf(@PathVariable("id") id: UUID?): ByteArray {
//        return service.findOne(id).makePdf().toByteArray()
//    }
//
//    @ResponseStatus(value = HttpStatus.OK)
//    @RequestMapping(value = ["/xml/{id}"], method = [RequestMethod.GET])
//    fun getXml(@PathVariable("id") id: UUID?): String {
//        return XmlDDIFragmentAssembler<QuestionItem>(service.findOne(id)).compileToXml()
//    }
//
//    init {
//        this.service = service
//    }
//}
