package no.nsd.qddt.controller

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.repository.CategoryRepository
import no.nsd.qddt.repository.handler.EntityAuditTrailListener
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
    @GetMapping("/category/revisions/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(
        @PathVariable uri: UUID,
        pageable: Pageable
    ): PagedModel<RepresentationModel<EntityModel<Category>>> {
        return super.getRevisions(uri, pageable)
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
    override fun getXml(@PathVariable uri: String): String {
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
    @PutMapping("/category/{uri}/children", produces = ["application/hal+json"])
    fun putChildren(
        @PathVariable uri: UUID,
        @RequestBody category: Category
    ): ResponseEntity<RepresentationModel<EntityModel<Category>>> {
        logger.debug("put category CategoryController...")

        var parent = repository.findById(uri).orElseThrow()
        val entity = repository.findById(category.id!!).orElseThrow()
        if (category.code!= null) {
            entity.code = category.code
        }
        parent.addChildren(entity)
        parent = repository.saveAndFlush(parent)

        return ResponseEntity.ok(entityModelBuilder(parent.children.last()))
    }

    override fun entityModelBuilder(entity: Category): RepresentationModel<EntityModel<Category>> {
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        logger.debug("entityModelBuilder Category : {}", uriId)
        val baseUrl = if (uriId.rev != null)
            "${baseUri}/category/revision/${uriId}"
        else
            "${baseUri}/category/${uriId.id}"
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
//            .link(Link.of("${baseUri}/category/topics/${uriId}","topics"))

            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(children, LinkRelation.of("children"))
            .build()
    }
}
