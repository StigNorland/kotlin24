package no.nsd.qddt.controller

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.User
import no.nsd.qddt.model.enums.CategoryKind
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.model.enums.ResponseKind
import no.nsd.qddt.repository.ResponseDomainRepository
import no.nsd.qddt.repository.handler.EntityAuditTrailListener.Companion.harvestCatCodes
import no.nsd.qddt.repository.handler.EntityAuditTrailListener.Companion.loadChildren
import no.nsd.qddt.repository.handler.EntityAuditTrailListener.Companion.populateCatCodes
import no.nsd.qddt.repository.projection.ManagedRepresentation
import no.nsd.qddt.repository.projection.UserListe
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.data.projection.ProjectionFactory
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayInputStream
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

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/responsedomain/revisions/{uuid}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uuid: UUID,pageable: Pageable): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
    }

    @GetMapping("/responsedomain/pdf/{uri}", produces = ["application/pdf"])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        logger.debug("PDF : {}", uri)
        return super.getPdf(uri)
    }

    @GetMapping("/responsedomain/xml/{uri}", produces = ["application/xml"])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

    @Transactional(propagation = Propagation.NESTED)
    @PutMapping("/responsedomain/{uri}", produces = ["application/hal+json"], consumes = ["application/hal+json","application/json"])
    fun putResponseDomain(@PathVariable uri: UUID, @RequestBody responseDomain: ResponseDomain):ResponseEntity<*> {
        return try {
            persistManagedRep(responseDomain)
            logger.debug(
                "harvestedCodes : {} : {}",
                responseDomain.name,
                responseDomain.codes.joinToString { it.value })

            val saved = repository.saveAndFlush(responseDomain)
            ResponseEntity(saved, HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity<String>(e.localizedMessage, HttpStatus.NOT_MODIFIED)
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    @PostMapping("/responsedomain")
    fun postResponseDomain(@RequestBody responseDomain: ResponseDomain): ResponseEntity<*> {
        return try {
            persistManagedRep(responseDomain)
            logger.debug(
                "harvestedCodes : {} : {}",
                responseDomain.name,
                responseDomain.codes.joinToString { it.value })

            val saved = repository.saveAndFlush(responseDomain)

            val currentuser = SecurityContextHolder.getContext().authentication.principal as User
            saved.modifiedBy = currentuser
            saved.agency = currentuser.agency
            saved.managedRepresentation!!.modifiedBy = currentuser
            ResponseEntity(saved, HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @ResponseBody
    @GetMapping("/responsedomain/{uri}", produces = ["application/hal+json"])
    fun getResponseDomain(@PathVariable uri: UUID):  RepresentationModel<*> {
        return entityModelBuilder(repository.findById(uri).orElseThrow())
    }

    @ResponseBody
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
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"responsedomain")
        logger.debug("EntModBuild ResponseDomain : {}", uriId)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        Hibernate.initialize(entity.managedRepresentation)

        repLoaderService.getRepository<Category>(ElementKind.CATEGORY).let { rr ->
            entity.managedRepresentation.children = loadChildren(entity.managedRepresentation,rr)
        }


        var _index = 0
        populateCatCodes(entity.managedRepresentation, _index,entity.codes)

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



    fun entityModelBuilder(entity: Category): RepresentationModel<EntityModel<Category>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"category")
        logger.debug("EntModBuild Category : {}", uriId)

        val children = when (entity.hierarchyLevel) {
            HierarchyLevel.GROUP_ENTITY -> {
                entity.children?.map {  entityModelBuilder(it) }
            }
            else -> mutableListOf()
        }
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(children!!, LinkRelation.of("children"))
            .build()
    }

    private fun persistManagedRep(entity: ResponseDomain) {
        entity.codes = harvestCatCodes(entity.managedRepresentation)
        logger.debug("persistManagedRep[0] : {} : {}", entity.managedRepresentation!!.name, entity.codes.joinToString { it.value })

        entity.managedRepresentation = entity.managedRepresentation!!.let{ manRep ->
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
            if (entity.changeKind.ordinal > 0 && entity.changeKind.ordinal < 4 )
                manRep.clone()
            else
                manRep
        }
        logger.debug("persistManagedRep[1] : {} : {}", entity.managedRepresentation!!.name, entity.codes.joinToString { it.value })

    }


}
