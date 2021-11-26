package no.nsd.qddt.controller

import no.nsd.qddt.model.builder.xml.XmlDDIFragmentAssembler
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.BaseMixedRepository
import org.hibernate.Hibernate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.history.Revision
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody


//@BasePathAwareController
abstract class AbstractRestController<T : AbstractEntityAudit>( val repository: BaseMixedRepository<T>) {

//    @Autowired
//    private val applicationContext: ApplicationContext? = null
    @Autowired
    private lateinit var pagedResourcesAssembler: PagedResourcesAssembler<T>

    @ResponseBody
    open fun getById(@PathVariable uri: String): ResponseEntity<EntityModel<T>> {
        logger.debug("getById : {}" , uri)
        val model = EntityModel.of(getByUri(uri))
//            .addIf()
        return ResponseEntity.ok(model)
    }
    @ResponseBody
    open fun getRevisions(@PathVariable uri: String, pageable: Pageable):  RepresentationModel<*>
    {
        val uriId = UriId.fromAny(uri)
        val qPage: Pageable = if (pageable.sort.isUnsorted) {
             PageRequest.of(pageable.pageNumber, pageable.pageSize,Sort.Direction.DESC,"modified")
        } else {
            pageable
        }
        logger.debug("getRevisions 1: {}" , qPage)

        if (uriId.rev != null) {
            val rev = repository.findRevision(uriId.id, uriId.rev!!)
                .orElse( repository.findLastChangeRevision(uriId.id).orElseThrow())
            return entityModelBuilder(rev)
        }
        else {
            val revisions = repository.findRevisions(uriId.id, qPage).map {
                    rev -> entityModelBuilder(rev)
            }
            val pagedResult = PagedModel.wrap(revisions.content, pageMetadataBuilder(revisions))
            return pagedResult
        }

    }



    open fun getPdf(@PathVariable uri: String): ByteArray {
        logger.debug("getPdf : {}", uri)
        return getByUri(uri).makePdf().toByteArray()
    }


    open fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        logger.debug("compileToXml : {}" ,uri)
        return ResponseEntity.ok(XmlDDIFragmentAssembler(getByUri(uri)).compileToXml())
    }

    private fun getByUri(uri: String): T {
        return getByUri(UriId.fromAny(uri))
    }


    private fun getByUri(uri: UriId): T {
        return if (uri.rev != null)
            repository.findRevision(uri.id, uri.rev!!).map { it.entity.version.rev = it.revisionNumber.get(); it.entity }.get()
        else
            repository.findById(uri.id).get()
    }

    private fun  pageMetadataBuilder(revisions: Page<RepresentationModel<EntityModel<T>>>): PagedModel.PageMetadata {
        return PagedModel.PageMetadata(revisions.size.toLong(),revisions.pageable.pageNumber.toLong(),revisions.totalElements,revisions.totalPages.toLong())
    }

    private fun entityModelBuilder(rev: Revision<Int, T>): RepresentationModel<EntityModel<T>> {
        rev.entity.version.rev = rev.revisionNumber.get()
        rev.entity.comments.size
        Hibernate.initialize(rev.entity.agency)
        Hibernate.initialize(rev.entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(rev.entity)
            .link(Link.of("/api/revisions/xxx/${rev.entity.id}:${rev.entity.version.rev}", "self"))
            .embed(rev.entity.agency,LinkRelation.of("agency"))
            .embed(rev.entity.modifiedBy,LinkRelation.of("modifiedBy"))
            .embed(rev.entity.comments,LinkRelation.of("comments"))
            .build()
    }

    companion object {
//        protected val logger = LoggerFactory.getLogger(AbstractRestController::class.java)

        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

}
