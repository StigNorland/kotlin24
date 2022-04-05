package no.nsd.qddt.controller

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.embedded.CategoryChildren
import no.nsd.qddt.model.embedded.UriId
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.model.enums.ResponseKind
import no.nsd.qddt.repository.ResponseDomainRepository
import no.nsd.qddt.repository.handler.EntityAuditTrailListener
import no.nsd.qddt.repository.handler.EntityAuditTrailListener.Companion.harvestCatCodes
import no.nsd.qddt.repository.handler.EntityAuditTrailListener.Companion.populateCatCodes
import no.nsd.qddt.repository.projection.ManagedRepresentation
import no.nsd.qddt.repository.projection.UserListe
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Pageable
import org.springframework.data.projection.ProjectionFactory
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import org.springframework.web.bind.annotation.*
import java.sql.SQLException
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
    override fun getPdf(@PathVariable uri: String): ResponseEntity<ByteArrayResource> {
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

            val saved = repository.saveAndFlush(responseDomain)

//            val loaded = repository.getById(saved.id!!)

            ResponseEntity(null, HttpStatus.ACCEPTED)

        } catch (e: Exception) {
            ResponseEntity<String>(e.localizedMessage, HttpStatus.NOT_MODIFIED)
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @PostMapping("/responsedomain")
    fun postResponseDomain(@RequestBody responseDomain: ResponseDomain): ResponseEntity<*> {
        return try {
            persistManagedRep(responseDomain)

            val saved = repository.saveAndFlush(responseDomain)

//            val currentUser = SecurityContextHolder.getContext().authentication.principal as User
//            saved.modifiedBy = currentUser
//            saved.agency = currentUser.agency
//            saved.managedRepresentation.modifiedBy = currentUser
//            repLoaderService.getRepository<Category>(ElementKind.CATEGORY).let { rr ->
//                saved.managedRepresentation.children =
//                    EntityAuditTrailListener.loadChildrenDefault(saved.managedRepresentation, rr)
//            }
            ResponseEntity(null, HttpStatus.CREATED)
        }catch (ex:DataIntegrityViolationException){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
            throw ex

        } catch (e: Exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
            ResponseEntity<String>(e.localizedMessage, HttpStatus.INTERNAL_SERVER_ERROR)
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
        return entityModelBuilder(managedRepresentationSaved)
    }


    override fun entityModelBuilder(entity: ResponseDomain): RepresentationModel<EntityModel<ResponseDomain>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"responsedomain")
        logger.debug("ModelBuilder ResponseDomain: {}", uriId)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        Hibernate.initialize(entity.managedRepresentation)

        val user =
            this.factory?.createProjection(UserListe::class.java, entity.modifiedBy)

            entity.managedRepresentation.version.rev = uriId.rev!!


            repLoaderService.getRepository<Category>(ElementKind.CATEGORY).let { rr ->
                entity.managedRepresentation.children =
                    EntityAuditTrailListener.loadChildrenDefault(entity.managedRepresentation, rr)
            }

            var _index = 0
            populateCatCodes(entity.managedRepresentation, _index, entity.codes)

        try {
            val managedRepresentation =
                this.factory?.createProjection(ManagedRepresentation::class.java, entity.managedRepresentation)

            return HalModelBuilder.halModel()
                .entity(entity)
                .link(Link.of(baseUrl))
                .embed(entity.agency!!, LinkRelation.of("agency"))
                .embed(user!!, LinkRelation.of("modifiedBy"))
                .embed(managedRepresentation!!, LinkRelation.of("managedRepresentation"))
                .build()
        } catch (ex: Exception) {
            logger.error(ex.localizedMessage)
            throw ex
        }
    }

    fun entityModelBuilder(entity: Category): RepresentationModel<EntityModel<Category>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"category")
        logger.debug("ModelBuilder Category : {}", uriId)

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

        entity.managedRepresentation = entity.managedRepresentation.let{ manRep ->
            manRep.name = entity.name
            manRep.label = capitalize(entity.name)
            manRep.changeComment = entity.changeComment
            manRep.changeKind = entity.changeKind
            manRep.xmlLang = entity.xmlLang
            manRep.version = entity.version
            manRep.description = entity.getAnchorLabels()
            if (manRep.hierarchyLevel == HierarchyLevel.GROUP_ENTITY)
                manRep.categoryChildren = manRep.children?.map {
                    CategoryChildren().apply {
                        uri = UriId().apply {id = it.id!!; rev = it.version.rev}
                        children = it
                    }
                }!!.toMutableList()
            if (entity.responseKind == ResponseKind.LIST) {
                manRep.inputLimit = entity.responseCardinality
            } else {
                entity.responseCardinality = manRep.inputLimit
            }
            if (entity.changeKind.ordinal in (1..3))
                manRep.clone()
            else
                manRep
        }
        logger.debug("persistManagedRep : {} : {}", entity.managedRepresentation.name, entity.codes.joinToString { it.value })

    }

    private fun capitalize(label:String): String {
        return label.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

}
