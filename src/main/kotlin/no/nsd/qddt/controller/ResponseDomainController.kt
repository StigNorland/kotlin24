package no.nsd.qddt.controller

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.model.enums.ResponseKind
import no.nsd.qddt.repository.CategoryRepository
import no.nsd.qddt.repository.ResponseDomainRepository
import no.nsd.qddt.repository.handler.EntityAuditTrailListener
import no.nsd.qddt.repository.handler.EntityAuditTrailListener.Companion.harvestCatCodes
import no.nsd.qddt.repository.handler.EntityAuditTrailListener.Companion.populateCatCodes
import no.nsd.qddt.repository.projection.CategoryListe
import no.nsd.qddt.repository.projection.ManagedRepresentation
import no.nsd.qddt.repository.projection.UserListe
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.projection.ProjectionFactory
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*


@BasePathAwareController
@Transactional(propagation = Propagation.REQUIRED)
class ResponseDomainController(@Autowired repository: ResponseDomainRepository) :
    AbstractRestController<ResponseDomain>(repository) {


    @Autowired
    private val factory: ProjectionFactory? = null

    @Transactional(propagation = Propagation.NESTED)
    @GetMapping("/responsedomain/revision/{uri}", produces = ["application/hal+json"])
    @ResponseBody
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @GetMapping("/responsedomain/revisions/{uri}", produces = ["application/hal+json."])
    @ResponseBody
    override fun getRevisions(@PathVariable uri: UUID,pageable: Pageable): PagedModel<RepresentationModel<EntityModel<ResponseDomain>>> {
        return super.getRevisions(uri, pageable)
    }

//    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/responsedomain/{uri}", produces = ["application/pdf"])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        logger.debug("PDF : {}", uri)
        return super.getPdf(uri)
    }

//    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/responsedomain/{uri}", produces = ["application/xml"])
    override fun getXml(@PathVariable uri: String): String {
        return super.getXml(uri)
    }

//    @Transactional(propagation = Propagation.NESTED)

    @PutMapping("/responsedomain/{uri}", produces = ["application/hal+json"], consumes = ["application/hal+json","application/json"])
    fun putResponseDomain(@PathVariable uri: UUID, @RequestBody responseDomain: ResponseDomain):ResponseEntity<*> {

        try {
//        responseDomain.codes = harvestCatCodes(responseDomain.managedRepresentation)
            persistManagedRep(responseDomain)
            logger.debug(
                "harvestedCodes : {} : {}",
                responseDomain.name,
                responseDomain.codes.joinToString { it.value })

            val saved = repository.save(responseDomain)

            var index = 0
            populateCatCodes(saved.managedRepresentation, index, saved.codes)
            logger.debug("populatedCodes : {} : {}", saved.name, saved.codes.joinToString { it.value })

            if (saved == null) {
                return ResponseEntity<ResponseDomain>(HttpStatus.NOT_MODIFIED)
            }
            return ResponseEntity<ResponseDomain>( HttpStatus.OK)
        } catch (e: Exception) {
            return ResponseEntity<ResponseDomain>( HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Modifying
    @PostMapping("/responsedomain")
    fun postResponseDomain(@RequestBody responseDomain: ResponseDomain): ResponseEntity<*> {

        try {
            persistManagedRep(responseDomain)
            logger.debug(
                "harvestedCodes : {} : {}",
                responseDomain.name,
                responseDomain.codes.joinToString { it.value })

            val saved = repository.save(responseDomain)

            var index = 0
            populateCatCodes(saved.managedRepresentation, index, saved.codes)
            logger.debug("populatedCodes : {} : {}", saved.name, saved.codes.joinToString { it.value })

            if (saved == null) {
                return ResponseEntity<ResponseDomain>(HttpStatus.NO_CONTENT)
            }
            return ResponseEntity(null, HttpStatus.CREATED)
        } catch (e: Exception) {
            return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

//    @Transactional
//    @ResponseBody
//    @GetMapping("/responsedomain/{uri}", produces = ["application/hal+json"])
//    fun getResponseDomain(@PathVariable uri: UUID):  RepresentationModel<*> {
////        logger.debug("getResponseDomain - harvestCode : {} : {}", responseDomain.name, responseDomain.codes.joinToString { it.value })
//        return entityModelBuilder(repository.findById(uri).orElseThrow())
//    }

//    @Transactional
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
        return entityModelBuilder(managedRepresentationSaved!!)
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
        var _index = 0
        populateCatCodes(entity.managedRepresentation, _index,entity.codes)

        val user =
            this.factory?.createProjection(UserListe::class.java, entity.modifiedBy)
        val managedRepresentation =
            this.factory?.createProjection(ManagedRepresentation::class.java, entity.managedRepresentation!!)

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
        val children = when (entity.hierarchyLevel) {
            HierarchyLevel.GROUP_ENTITY -> {
                entity.children.size
                entity.children.map { entityModelBuilder(it) }
            }
            else -> mutableListOf()
        }
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(children, LinkRelation.of("children"))
            .build()
    }

    private fun persistManagedRep(entity: ResponseDomain) {
        entity.codes = harvestCatCodes(entity.managedRepresentation)
        logger.debug("persistManagedRep[0] : {} : {}", entity.managedRepresentation!!.name, entity.codes.joinToString { it.value })

        entity.managedRepresentation!!.let{ manRep ->
            manRep.name = entity.name
            manRep.changeComment = entity.changeComment
            manRep.changeKind = entity.changeKind
            manRep.xmlLang = entity.xmlLang
            manRep.version = entity.version
            manRep.description = entity.getAnchorLabels()
            if (entity.responseKind == ResponseKind.LIST) {
                manRep.inputLimit = entity.responseCardinality
            } else {
                entity.responseCardinality = manRep.inputLimit
            }
//            val result = categoryRepository!!.save(manRep)
            manRep
        }
        logger.debug("persistManagedRep[1] : {} : {}", entity.managedRepresentation!!.name, entity.codes.joinToString { it.value })

    }


}
