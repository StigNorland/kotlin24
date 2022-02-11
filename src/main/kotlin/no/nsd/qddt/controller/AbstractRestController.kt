package no.nsd.qddt.controller

import no.nsd.qddt.model.builder.xml.XmlDDIFragmentAssembler
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.model.interfaces.RepLoaderService
import no.nsd.qddt.repository.BaseMixedRepository
import org.hibernate.Hibernate
import org.hibernate.envers.AuditReaderFactory
import org.hibernate.envers.query.AuditEntity
import org.hibernate.envers.query.AuditQuery
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.history.Revision
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.hateoas.server.mvc.BasicLinkBuilder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


abstract class AbstractRestController<T : AbstractEntityAudit>(val repository: BaseMixedRepository<T>) {

    val baseUri get() = BasicLinkBuilder.linkToCurrentMapping()

    @PersistenceContext
    protected val entityManager: EntityManager? = null

    @Autowired
    protected val applicationContext: ApplicationContext? = null

    val repLoaderService get() = applicationContext?.getBean("repLoaderService") as RepLoaderService

    @ResponseBody
    open fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        val uriId = UriId.fromAny(uri)

        return if (uriId.rev != null) {
            logger.debug("getRevisions entityRevisionModelBuilder")
            val rev = repository.findRevision(uriId.id, uriId.rev!!)
                .orElse(repository.findLastChangeRevision(uriId.id)
                .orElseThrow())
            entityRevisionModelBuilder(rev)
        } else {
            HalModelBuilder.emptyHalModel().build<EntityModel<T>>()
        }
    }


    @ResponseBody
    open fun getRevisions(@PathVariable uuid: UUID, pageable: Pageable): PagedModel<RepresentationModel<EntityModel<T>>> {
        val qPage: Pageable = if (pageable.sort.isUnsorted) {
            PageRequest.of(pageable.pageNumber, pageable.pageSize*2, Sort.Direction.DESC, "modified")
        } else {
            pageable
        }
////        val instanse = ofClass.getConstructor().newInstance()
////        if (instanse.classKind =="")
//        val auditReader = AuditReaderFactory.get(entityManager)
//
//        val revQuery: AuditQuery = auditReader.createQuery().forRevisionsOfEntity(ofClass, false)
//            .add(AuditEntity.property("changeKind").ne(IBasedOn.ChangeKind.IN_DEVELOPMENT))
////            .add(AuditEntity.property("CLASS_KIND").eq(instanse.classKind))
//            .add(AuditEntity.property("id").eq(uuid))
//
//
//
//        val result = revQuery.resultList
//            .filterIndexed { index, _ -> index > qPage.offset && index < qPage.offset + qPage.pageSize }
//            .map {
//            val rev = auditReader.createQuery().forEntitiesAtRevision(ofClass, (it as RevisionEntityImpl).id )
//                .add(AuditEntity.property("id").eq(uuid))
//                .singleResult.apply {
//                (this as T).version.rev =  it.id
//            }
//            entityModelBuilder(rev as T)
//        }
//
//        return PagedModel.of(result, PagedModel.PageMetadata(result.size.toLong(), 1L, result.size.toLong()))



        logger.debug("getRevisions PagedModel: {}", qPage)
        val revisions = repository.findRevisions(uuid, qPage)
            .filter {
                    it.entity.changeKind.ordinal <  IBasedOn.ChangeKind.UPDATED_PARENT.ordinal ||
                    it.entity.changeKind.ordinal > IBasedOn.ChangeKind.IN_DEVELOPMENT.ordinal }
            .map { rev -> entityRevisionModelBuilder(rev) }.toList()
        return PagedModel.of(revisions, PagedModel.PageMetadata(revisions.size.toLong(), 1L, revisions.size.toLong()))
    }

    @ResponseBody
    open fun getRevisionsByParent(
        @PathVariable uri: String,
        ofClass: Class<T>,
        pageable: Pageable?
    ): RepresentationModel<*> {
        val uriId = UriId.fromAny(uri)

        logger.debug("getRevisionByParent 1: {}", uriId)
        val auditReader = AuditReaderFactory.get(entityManager)

        val query: AuditQuery = auditReader.createQuery().forEntitiesAtRevision(ofClass, uriId.rev)
            .add(AuditEntity.property("parent_id").eq(uriId.id))

        val result = query.resultList.map { rev -> entityModelBuilder(rev as T) }

        return PagedModel.of(result, PagedModel.PageMetadata(result.size.toLong(), 1L, result.size.toLong()))

//        return PagedModel.of(null)
    }


    open fun getPdf(@PathVariable uri: String): ByteArray {
        logger.debug("getPdf : {}", uri)
        return getByUri(uri).makePdf().toByteArray()
    }


    open fun getXml(@PathVariable uri: String): String {
        val xml = XmlDDIFragmentAssembler(getByUri(uri)).compileToXml()
        logger.debug("compiledToXml : {}", xml)
        return xml
    }


    open fun entityModelBuilder(entity: T): RepresentationModel<EntityModel<T>> {
        logger.debug("entityModelBuilder(T) : {}", entity.name)
        val baseUri = BasicLinkBuilder.linkToCurrentMapping()

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
//        Hibernate.initialize(entity.parent)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of("${baseUri}/study/${entity.id}"))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .build()
    }


    open fun getByUri(uri: String): T {
        logger.debug("getByUri : {}", uri)
        return getByUri(UriId.fromAny(uri))
    }

    protected fun pageMetadataBuilder(revisions: Page<RepresentationModel<EntityModel<T>>>): PagedModel.PageMetadata {
        return PagedModel.PageMetadata(
            revisions.size.toLong(),
            revisions.pageable.pageNumber.toLong(),
            revisions.totalElements,
            revisions.totalPages.toLong()
        )
    }

    protected fun entityRevisionModelBuilder(rev: Revision<Int, T>): RepresentationModel<EntityModel<T>> {
        rev.entity.version.rev = rev.revisionNumber.get()
        return entityModelBuilder(rev.entity)
    }

    private fun getByUri(uri: UriId): T {
        logger.debug("_getByUri : {}", uri)
        return if (uri.rev != null)
            repository.findRevision(uri.id, uri.rev!!).map {
                logger.debug("_getByUri : {}", it.entity.version.rev)
                it.entity.version.rev = it.revisionNumber.get()
                it.entity
            }.orElseThrow()
        else
            repository.findById(uri.id).orElseThrow()
    }


    companion object {

        val logger: Logger = LoggerFactory.getLogger(this::class.java)

        fun <T : AbstractEntityAudit> loadRevisionEntity(uri: UriId,repository: RevisionRepository<T, UUID, Int>): T {
            logger.debug("loadRevisionEntity {}", uri )
            return with(uri) {
                if (rev != null && rev != 0)
                    repository.findRevision(id, rev!!).map {
                        it.entity.version.rev = it.revisionNumber.get()
                        it.entity
                    }.get()
                else
                    repository.findLastChangeRevision(id).map {
                        it.entity.version.rev = it.revisionNumber.get()
                        it.entity
                    }.get()
            }
        }
    }


}
