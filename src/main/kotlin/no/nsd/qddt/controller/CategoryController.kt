package no.nsd.qddt.controller

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.repository.CategoryRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*


@BasePathAwareController
class CategoryController(@Autowired repository: CategoryRepository) : AbstractRestController<Category>(repository) {

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/category/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/category/revisions/{uuid}", produces = ["application/hal+json"])
    override fun getRevisions(
        @PathVariable uuid: UUID,
        pageable: Pageable
    ): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/category/revisions/byparent/{uri}", produces = ["application/hal+json"])
    fun getStudies(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*> {
        logger.debug("get category by parent rev...")
        return super.getRevisionsByParent(uri, Category::class.java, pageable)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/category/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/category/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

    @ResponseBody
    @Modifying
    @PutMapping("/category/{uuid}",produces = ["application/hal+json", "application/text"], consumes = ["application/hal+json","application/json"])
    fun putCategory(@PathVariable uuid: UUID, @RequestBody category: Category): ResponseEntity<*> {

        try {

            val saved = repository.save(category)

            return ResponseEntity<Category>(null, HttpStatus.OK)
        } catch (e: Exception) {
            return ResponseEntity<String>(e.localizedMessage, HttpStatus.CONFLICT)
        }
    }

    @ResponseBody
    @Modifying
    @PostMapping("/category",produces = ["application/hal+json", "application/text"], consumes = ["application/hal+json","application/json"])
    fun postCategory(@RequestBody category: Category): ResponseEntity<*> {

        try {

            val saved = repository.save(category)

            return ResponseEntity<Category>(saved, HttpStatus.CREATED)
        } catch (e: Exception) {
            return ResponseEntity<String>(e.localizedMessage, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    @PutMapping("/category/{uuid}/children", produces = ["application/hal+json"])
    fun putChildren(
        @PathVariable uuid: UUID,
        @RequestBody category: Category
    ): ResponseEntity<RepresentationModel<EntityModel<Category>>> {
        logger.debug("put category CategoryController...")

        var parent = repository.findById(uuid).orElseThrow()
        val entity = repository.findById(category.id!!).orElseThrow()
        if (category.code!= null) {
            entity.code = category.code
        }
        parent.addChildren(entity)
        parent = repository.saveAndFlush(parent)

        return ResponseEntity.ok(entityModelBuilder(parent.children.last()))
    }

    override fun entityModelBuilder(entity: Category): RepresentationModel<EntityModel<Category>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"category")
        logger.debug("entityModelBuilder Category : {}", uriId)

        val children = when (entity.hierarchyLevel) {
            HierarchyLevel.GROUP_ENTITY -> {
                entity.children.size
                entity.children.map { entityModelBuilder(it) }
            }
            else -> mutableListOf()
        }
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))

            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(children, LinkRelation.of("children"))
            .build()
    }
}
