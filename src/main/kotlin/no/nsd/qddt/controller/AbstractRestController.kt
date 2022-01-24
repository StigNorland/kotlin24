package no.nsd.qddt.controller

import no.nsd.qddt.model.builder.xml.XmlDDIFragmentAssembler
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.BaseMixedRepository
import org.hibernate.Hibernate
import org.hibernate.envers.AuditReaderFactory
import org.hibernate.envers.query.AuditEntity
import org.hibernate.envers.query.AuditQuery
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.history.Revision
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.hateoas.server.core.EmbeddedWrapper
import org.springframework.hateoas.server.mvc.BasicLinkBuilder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*
import javax.persistence.EntityManager


abstract class AbstractRestController<T : AbstractEntityAudit>( val repository: BaseMixedRepository<T>) {

    val baseUri get() = BasicLinkBuilder.linkToCurrentMapping()

    @Autowired
    private val entityManager: EntityManager? = null

    @ResponseBody
    open fun getRevision(@PathVariable uri: String):  RepresentationModel<*>
    {
        val uriId = UriId.fromAny(uri)

        return if (uriId.rev != null) {
            logger.debug("getRevisions entityRevisionModelBuilder")
            val rev = repository.findRevision(uriId.id, uriId.rev!!).orElse( repository.findLastChangeRevision(uriId.id).orElseThrow())
            entityRevisionModelBuilder(rev)
        } else {
            HalModelBuilder.emptyHalModel().build<EntityModel<T>>()
        }
    }
    @ResponseBody
    open fun getRevisions(@PathVariable uri: UUID, pageable: Pageable):  RepresentationModel<*>
    {
        val qPage: Pageable = if (pageable.sort.isUnsorted) {
             PageRequest.of(pageable.pageNumber, pageable.pageSize,Sort.Direction.DESC,"modified")
        } else {
            pageable
        }

        logger.debug("getRevisions PagedModel: {}" , qPage)
        val revisions = repository.findRevisions(uri, qPage).map { rev -> entityRevisionModelBuilder(rev) }
        return PagedModel.of(revisions.content, pageMetadataBuilder(revisions))
    }

    @ResponseBody
    open fun getRevisionsByParent(@PathVariable uri: String, ofClass: Class<T>, pageable: Pageable?): RepresentationModel<*> {
        val uriId = UriId.fromAny(uri)

        logger.debug("getRevisionByParent 1: {}" , uriId)
        val auditReader = AuditReaderFactory.get(entityManager)

        val query: AuditQuery = auditReader.createQuery().forEntitiesAtRevision(ofClass,uriId.rev)
            .add(AuditEntity.property("parent_id").eq(uriId.id))

        val result = query.resultList.map { rev -> entityModelBuilder(rev as T) }
//        return PagedModel.of(revisions.content, pageMetadataBuilder(revisions))
        return PagedModel.of(result, PagedModel.PageMetadata(result.size.toLong(),1L, result.size.toLong()))

    }


    open fun getPdf(@PathVariable uri: String): ByteArray {
        logger.debug("getPdf : {}", uri)
        return getByUri(uri).makePdf().toByteArray()
    }


    open fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        logger.debug("compileToXml : {}" ,uri)
        return ResponseEntity.ok(XmlDDIFragmentAssembler(getByUri(uri)).compileToXml())
    }


    protected fun getByUri(uri: String): T {
        logger.debug("getByUri : {}" , uri)
        return getByUri(UriId.fromAny(uri))
    }


    protected fun getByUri(uri: UriId): T {
        logger.debug("_getByUri : {}" , uri)
        return if (uri.rev != null)
            repository.findRevision(uri.id, uri.rev!!).map {
                logger.debug("_getByUri : {}" , it.entity.version.rev)
                it.entity.version.rev = it.revisionNumber.get()
                it.entity
            }.orElseThrow()
        else
            repository.findById(uri.id).orElseThrow()
    }

    protected fun pageMetadataBuilder(revisions: Page<RepresentationModel<EntityModel<T>>>): PagedModel.PageMetadata {
        return PagedModel.PageMetadata(revisions.size.toLong(),revisions.pageable.pageNumber.toLong(),revisions.totalElements,revisions.totalPages.toLong())
    }

    open fun entityModelBuilder(entity: T): RepresentationModel<EntityModel<T>> {
        logger.debug("entityModelBuilder : {}" , entity.id)
        val baseUri = BasicLinkBuilder.linkToCurrentMapping()

        entity.comments.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
//        Hibernate.initialize(entity.parent)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of("${baseUri}/study/${entity.id}"))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .build()
    }


    fun entityRevisionModelBuilder(rev: Revision<Int, T>): RepresentationModel<EntityModel<T>> {
        rev.entity.version.rev = rev.revisionNumber.get()
        return entityModelBuilder(rev.entity)
    }


    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }


}
