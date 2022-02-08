package no.nsd.qddt.controller

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.CategoryRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
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
    fun getRevisions(
        @PathVariable uri: UUID,
        pageable: Pageable
    ): RepresentationModel<*> {
        return super.getRevisions(uri, pageable,Category::class.java)
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

        entity.comments.size
        entity.children.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
//            .link(Link.of("${baseUri}/category/topics/${uriId}","topics"))

            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.children.map {
                entityModelBuilder(it)
            }, LinkRelation.of("children"))
            .build()
    }
}
