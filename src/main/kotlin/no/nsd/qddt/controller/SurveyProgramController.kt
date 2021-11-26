package no.nsd.qddt.controller

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.SurveyProgramRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.history.Revision
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*


//@RepositoryRestController
@BasePathAwareController
class SurveyProgramController(@Autowired repository: SurveyProgramRepository): AbstractRestController<SurveyProgram>(repository) {
// https://docs.spring.io/spring-hateoas/docs/current/reference/html/#fundamentals.representation-models

//    @GetMapping("/surveyprogram/{uri}/{rev}", produces = ["application/hal+json"] )
//    fun getById(@PathVariable uri: UUID,@PathVariable rev: Long ): ResponseEntity<EntityModel<SurveyProgram>> {
//        return super.getById("$uri:$rev")
//    }

    @GetMapping("/surveyprogram/studies/{uri}", produces = ["application/hal+json"])
    @Transactional
    fun getStudies(@PathVariable uri: UUID): ResponseEntity<List<EntityModel<Study>>> {
        logger.debug("get studies controller...")
        val result = repository.findById(uri).get().children
        val entities = result.map {
            EntityModel.of(it)
        }
        return ResponseEntity.ok(entities)
    }

    @GetMapping("/pdf/surveyprogram/{uri}/{rev}", produces = [MediaType.APPLICATION_PDF_VALUE])
    fun getPdf(@PathVariable uri: UUID, @PathVariable rev: Long): ByteArray {
        logger.debug("get pdf controller...")
        return super.getPdf("$uri:$rev")
    }

    @GetMapping("/xml/surveyprogram/{uri}/{rev}", produces = [MediaType.APPLICATION_XML_VALUE])
    fun getXml(@PathVariable uri: UUID, @PathVariable rev: Long): ResponseEntity<String> {
        return super.getXml("$uri:$rev")
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/revisions/surveyprogram/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable):RepresentationModel<*> {
        val uriId = UriId.fromAny(uri)
        val qPage: Pageable = if (pageable.sort.isUnsorted) {
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.Direction.DESC, "RevisionNumber")
        } else {
            pageable
        }

        if (uriId.rev != null) {
            val rev = repository.findRevision(uriId.id, uriId.rev!!).get()
            return entityModelBuilder(rev)
        }
        else {
            val revisions = repository.findRevisions(uriId.id, qPage).map { rev ->
                entityModelBuilder(rev)
            }
            return PagedModel.wrap(revisions.content, pageMetadataBuilder(revisions))
        }
    }

    private fun  pageMetadataBuilder(revisions: Page<RepresentationModel<EntityModel<SurveyProgram>>>): PagedModel.PageMetadata {
        return PagedModel.PageMetadata(revisions.size.toLong(),revisions.pageable.pageNumber.toLong(),revisions.totalElements,revisions.totalPages.toLong())
    }

    private fun entityModelBuilder(rev: Revision<Int, SurveyProgram>): RepresentationModel<EntityModel<SurveyProgram>> {
        rev.entity.version.rev = rev.revisionNumber.get()
        rev.entity.children.size
        rev.entity.authors.size
        rev.entity.comments.size
        Hibernate.initialize(rev.entity.agency)
        Hibernate.initialize(rev.entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(rev.entity)
            .link(Link.of("/api/revisions/surveyprogram/${rev.entity.id}:${rev.entity.version.rev}", "self"))
            .embed(rev.entity.agency,LinkRelation.of("agency"))
            .embed(rev.entity.modifiedBy,LinkRelation.of("modifiedBy"))
            .embed(rev.entity.comments,LinkRelation.of("comments"))
            .embed(rev.entity.authors,LinkRelation.of("authors"))
            .embed(rev.entity.children.map {
                Hibernate.initialize(it.agency)
                Hibernate.initialize(it.modifiedBy)
                HalModelBuilder.halModel()
                    .entity(it)
                    .link(Link.of("/api/revisions/study/${it.id}:${rev.entity.version.rev}", "self"))
                    .embed(it.agency,LinkRelation.of("agency"))
                    .embed(it.modifiedBy,LinkRelation.of("modifiedBy"))
                    .build<EntityModel<Study>>()

            }, LinkRelation.of("studies"))
            .build()
    }

//    fun <T> initializeAndUnproxy(entity: T?): T? {
//        var entity: T? = entity ?: return null
//        Hibernate.initialize(entity)
//        if (entity is HibernateProxy) {
//            entity = (entity as HibernateProxy).hibernateLazyInitializer
//                .implementation as T
//        }
//        return entity
//    }
}
