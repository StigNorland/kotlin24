package no.nsd.qddt.domain.category.web

import no.nsd.qddt.domain.category.Category
import no.nsd.qddt.domain.category.CategoryService
import no.nsd.qddt.domain.category.json.CategoryJsonEdit
import no.nsd.qddt.domain.classes.xml.XmlDDIFragmentAssembler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */
@RestController
@RequestMapping("/category")
class CategoryController @Autowired constructor(private val service: CategoryService) {
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = ["{id}"], method = [RequestMethod.GET])
    operator fun get(@PathVariable("id") id: UUID): CategoryJsonEdit {
        return CategoryJsonEdit(service.findOne(id))
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = [""], method = [RequestMethod.POST])
    fun update(@RequestBody category: Category): CategoryJsonEdit {
        return CategoryJsonEdit(service.save(category))
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = ["/create"], method = [RequestMethod.POST])
    fun create(@RequestBody category: Category): CategoryJsonEdit {
        return CategoryJsonEdit(service.save(category))
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = ["/delete/{id}"], method = [RequestMethod.DELETE])
    fun delete(@PathVariable("id") id: UUID) {
        service.delete(id)
    }

    @RequestMapping(
        value = ["/page/search"],
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getBy(
        @RequestParam(value = "level", defaultValue = "") level: String,
        @RequestParam(value = "categoryKind", defaultValue = "") categoryKind: String,
        @RequestParam(value = "label", defaultValue = "") name: String?,
        @RequestParam(value = "description", defaultValue = "") description: String?,
        @RequestParam(value = "xmlLang", defaultValue = "") xmlLang: String?,
        pageable: Pageable?,
        assembler: PagedResourcesAssembler<CategoryJsonEdit>
    ): PagedModel<EntityModel<CategoryJsonEdit>> {
        val categories = service.findBy(level, categoryKind, name, description, xmlLang, pageable)
            .map { converter: Category? -> CategoryJsonEdit(converter) }
        return assembler.toModel(categories)
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = ["/xml/{id}"], method = [RequestMethod.GET])
    fun getXml(@PathVariable("id") id: UUID): String {
        return XmlDDIFragmentAssembler(service.findOne<Category>(id)).compileToXml()
    }
}
