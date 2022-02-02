package no.nsd.qddt.controller

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.repository.ResponseDomainRepository
import no.nsd.qddt.repository.projection.CategoryListe
import no.nsd.qddt.repository.projection.UserListe
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.projection.ProjectionFactory
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*


@BasePathAwareController
class ResponseDomainController(@Autowired repository: ResponseDomainRepository) :
    AbstractRestController<ResponseDomain>(repository) {

    @Autowired
    private val factory: ProjectionFactory? = null

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/responsedomain/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/responsedomain/revisions/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: UUID, pageable: Pageable): RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

    @GetMapping("/responsedomain/{uri}", produces = ["application/pdf"])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        logger.debug("PDF : {}", uri)
        return super.getPdf(uri)
    }

    @GetMapping("/responsedomain/{uri}", produces = ["application/xml"])
    override fun getXml(@PathVariable uri: String): String {
        return super.getXml(uri)
    }

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @PutMapping("/responsedomain/{uri}", produces = ["application/hal+json"])
    fun putResponseDomain(@PathVariable uri: UUID, @RequestBody responseDomain: ResponseDomain): ResponseDomain {
        responseDomain.codes = harvestCatCodes(responseDomain.managedRepresentation)
        logger.debug(
            "putResponseDomain - harvestCode : {} : {}",
            responseDomain.name,
            responseDomain.codes.joinToString { it.value })
        val saved = repository.save(responseDomain)
        var _index = 0
        populateCatCodes(saved.managedRepresentation, _index, saved.codes)
        logger.debug("putResponseDomain - saved : {} : {}", saved.name, saved.codes.joinToString { it.value })

        return saved
    }

//    @Transactional
//    @ResponseBody
//    @GetMapping("/responsedomain/{uri}", produces = ["application/hal+json"])
//    fun getResponseDomain(@PathVariable uri: UUID):  RepresentationModel<*> {
////        logger.debug("getResponseDomain - harvestCode : {} : {}", responseDomain.name, responseDomain.codes.joinToString { it.value })
//        return entityModelBuilder(repository.findById(uri).orElseThrow())
//    }

    @Transactional
    @ResponseBody
    @Modifying
    @PutMapping("/responsedomain/{uri}/managedrepresentation", produces = ["application/hal+json"])
    fun putManagedRepresentation(
        @PathVariable uri: UUID,
        @RequestBody managedRepresentation: Category
    ): RepresentationModel<*> {

        val domain = repository.findById(uri).orElseThrow()
        domain.managedRepresentation = managedRepresentation
        val managedRepresentationSaved = repository.saveAndFlush(domain).managedRepresentation
        return entityModelBuilder(managedRepresentationSaved)
    }

    private fun harvestCatCodes(current: Category?): MutableList<Code> {
        val tmpList: MutableList<Code> = mutableListOf()
        if (current == null) return tmpList
        if (current.hierarchyLevel == HierarchyLevel.ENTITY) {
            tmpList.add((current.code ?: Code("")))
        }
        current.children.forEach { tmpList.addAll(harvestCatCodes(it)) }
        return tmpList
    }

    override fun entityModelBuilder(entity: ResponseDomain): RepresentationModel<EntityModel<ResponseDomain>> {
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        logger.debug("entityModelBuilder ResponseDomain : {} {}", uriId, entity.codes.joinToString { it.value })
        val baseUrl = if (uriId.rev != null)
            "${baseUri}/responsedomain/revision/${uriId}"
        else
            "${baseUri}/responsedomain/${uriId.id}"
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        Hibernate.initialize(entity.managedRepresentation)
        entity.managedRepresentation.children.size
        var index = 0
        populateCatCodes(entity.managedRepresentation, index, entity.codes)
        val user = this.factory?.createProjection<UserListe>(UserListe::class.java, entity.modifiedBy)
        val managedRepresentation =
            this.factory?.createProjection(CategoryListe::class.java, entity.managedRepresentation)

        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(user!!, LinkRelation.of("modifiedBy"))
            .embed(managedRepresentation!!, LinkRelation.of("managedRepresentation"))
            .build()
    }

    fun entityModelBuilder(entity: Category): RepresentationModel<EntityModel<Category>> {
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        logger.debug("entityModelBuilder Category : {} : {}", uriId, entity.code)
        val baseUrl = if (uriId.rev != null)
            "${baseUri}/category/revision/${uriId}"
        else
            "${baseUri}/category/${uriId.id}"
        entity.children.size
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.children.map {
                entityModelBuilder(it)
            }, LinkRelation.of("children"))
            .build()
    }

    private fun populateCatCodes(current: Category?, _index: Int, codes: List<Code>): Int {
        if (current == null) return _index

        var index = _index

        if (current.hierarchyLevel == HierarchyLevel.ENTITY) {
            try {
//                log.debug(codes[index].toString())
                current.code = codes[index++]
            } catch (iob: IndexOutOfBoundsException) {
                current.code = Code()
            } catch (ex: Exception) {
                logger.error(ex.localizedMessage)
                current.code = Code()
            }
        }
        current.children.forEach {
            index = populateCatCodes(it, index, codes)
        }
        return index
    }
}
