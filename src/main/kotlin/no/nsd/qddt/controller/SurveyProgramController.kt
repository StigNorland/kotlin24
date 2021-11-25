package no.nsd.qddt.controller

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.model.classes.ModelRevisionResults
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.SurveyProgramRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.*
import org.springframework.data.rest.core.UriToEntityConverter
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.hateoas.server.core.EmbeddedWrapper
import org.springframework.hateoas.server.core.EmbeddedWrappers
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
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable):  RepresentationModel<EntityModel<SurveyProgram>>{
        val uriId = UriId.fromAny(uri)
        val qPage: Pageable = if (pageable.sort.isUnsorted) {
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.Direction.DESC, "RevisionNumber")
        } else {
            pageable
        }
        var wrappers = EmbeddedWrappers(false)

        if (uriId.rev != null) {
            val rev = repository.findRevision(uriId.id, uriId.rev!!).get()
            rev.entity.version.rev = rev.revisionNumber.get()
            val item = EntityModel.of<SurveyProgram>(
                rev.entity,
                Link.of("/api/revisions/surveyprogram/${rev.entity.id}:${rev.entity.version.rev}", "self")
            )
            rev.entity.children.size
            rev.entity.authors.size
            rev.entity.comments.size
                Hibernate.initialize(rev.entity.agency)
                Hibernate.initialize(rev.entity.modifiedBy)
            return HalModelBuilder.halModel(wrappers)
                .entity(item)
                .embed(rev.entity.agency,LinkRelation.of("agecy"))
                .embed(rev.entity.modifiedBy,LinkRelation.of("modifiedBy"))
                .embed(rev.entity.comments,LinkRelation.of("comments"))
                .embed(rev.entity.authors,LinkRelation.of("authors"))
                .embed(rev.entity.children, LinkRelation.of("studies"))
                .link( Link.of("/api/revisions/surveyprogram/${rev.entity.id}:${rev.entity.version.rev}/children", "studies"))
                .build()
        }
        else {
            val revisions = repository.findRevisions(uriId.id, qPage).map { rev ->
                rev.entity.version.rev = rev.revisionNumber.get()
                HalModelBuilder.halModel(wrappers)
                    .entity(rev.entity)
                    .link(Link.of("/api/revisions/surveyprogram/${rev.entity.id}:${rev.entity.version.rev}", "self"))
                    .embed(rev.entity.agency,LinkRelation.of("agnecy"))
                    .embed(rev.entity.modifiedBy)
                    .embed(rev.entity.authors)
                    .embed(rev.entity.children, LinkRelation.of("children"))
                    .build<EntityModel<SurveyProgram>>()

            }
            return HalModelBuilder.halModel()
                .entity(revisions.pageable)
                .embed(revisions.stream())
                .build()
        }


//                Hibernate.initialize(rev.entity.children)
//                Hibernate.initialize(rev.entity.authors)
//                Hibernate.initialize(rev.entity.agency)
//                Hibernate.initialize(rev.entity.modifiedBy)

//
//elements.add(wrappers.wrap(new Product("Product1a"), LinkRelation.of("all")));
//elements.add(wrappers.wrap(new Product("Product2a"), LinkRelation.of("purchased")));
//elements.add(wrappers.wrap(new Product("Product1b"), LinkRelation.of("all")));


//        return try {
//            return HalModelBuilder.emptyHalModel()
//                .embed(result.stream())
//                .
//                .map {
//
//                    it.content?.children?.size
//                    it.content?.authors?.size
//                    Hibernate.initialize(it.content?.agency)
//                    Hibernate.initialize(it.content?.modifiedBy)
//                    it
//
//                }).build()
//        } catch (ex: Exception) {
//            with(logger) { error(ex.localizedMessage) }
//            HalModelBuilder.emptyHalModel().build()
//        }
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
