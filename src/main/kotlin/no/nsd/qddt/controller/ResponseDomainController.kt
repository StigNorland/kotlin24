package no.nsd.qddt.controller

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.Publication
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.repository.ResponseDomainRepository
import no.nsd.qddt.repository.handler.EntityAuditTrailListener
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*


@BasePathAwareController
class ResponseDomainController(@Autowired repository: ResponseDomainRepository): AbstractRestController<ResponseDomain>(repository) {

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/responsedomain/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String):RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/responsedomain/revisions/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: UUID, pageable: Pageable):RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

    @GetMapping("/responsedomain/{uri}", produces = ["application/pdf"])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        logger.debug("PDF : {}", uri)
        return super.getPdf(uri)
    }

    @GetMapping("/responsedomain/{uri}", produces = ["application/xml"])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return  super.getXml(uri)
    }

    @Transactional
    @ResponseBody
    @Modifying
    @PutMapping("/responsedomain/{uri}", produces = ["application/hal+json"])
    fun putChildren(@PathVariable uri: UUID, @RequestBody responseDomain: ResponseDomain):  RepresentationModel<*> {
        responseDomain.codes = harvestCatCodes(responseDomain.managedRepresentation)
        logger.debug("putChildren - harvestCode : {} : {}", responseDomain.name, responseDomain.codes.joinToString { it.value })

//        var domain =  repository.findById(uri).orElseThrow()
//        domain.addChildren(category)
//        val topicSaved = repository.saveAndFlush(domain).children.last() as Category
        return entityModelBuilder(repository.saveAndFlush(responseDomain))
    }


    private fun harvestCatCodes(current: Category?): MutableList<Code> {
        val tmpList: MutableList<Code> = mutableListOf()
        if (current == null) return tmpList
        if (current.hierarchyLevel == HierarchyLevel.ENTITY) {
            tmpList.add((current.code?: Code("")))
        }
        current.children.forEach {  tmpList.addAll(harvestCatCodes(it)) }
        return tmpList
    }

    override fun entityModelBuilder(entity: ResponseDomain): RepresentationModel<EntityModel<ResponseDomain>> {
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        logger.debug("entityModelBuilder ResponseDomain : {}" , uriId)
        val baseUrl = if(uriId.rev != null)
            "${baseUri}/responsedomain/revision/${uriId}"
        else
            "${baseUri}/responsedomain/${uriId.id}"
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        Hibernate.initialize(entity.managedRepresentation)
        entity.managedRepresentation?.children?.size
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.managedRepresentation?:{},LinkRelation.of("managedRepresentation"))
            .build()
    }
}
