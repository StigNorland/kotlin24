package no.nsd.qddt.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import no.nsd.qddt.model.Study
import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.SurveyProgramRepository
import no.nsd.qddt.repository.projection.AgencyListe
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
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule.HalHandlerInstantiator
import org.springframework.hateoas.server.core.EvoInflectorLinkRelationProvider
import org.springframework.hateoas.server.mvc.BasicLinkBuilder
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*


//@RepositoryRestController
@BasePathAwareController
class SurveyProgramController(@Autowired repository: SurveyProgramRepository): AbstractRestController<SurveyProgram>(repository) {
// https://docs.spring.io/spring-hateoas/docs/current/reference/html/#fundamentals.representation-models

//    @GetMapping("/surveyprogram/{uri}/{rev}", produces = ["application/hal+json"] )
//    fun getById(@PathVariable uri: UUID,@PathVariable rev: Int ): ResponseEntity<EntityModel<SurveyProgram>> {
//        return super.getById("$uri:$rev")
//    }

    @GetMapping("/surveyprogram/studies/{uri}", produces = ["application/prs.hal-forms+json"])
    @Transactional
    fun getStudies(@PathVariable uri: String): RepresentationModel<*> {
        logger.debug("get studies SurveyProgramController...")
//        val baseUri = BasicLinkBuilder.linkToCurrentMapping()
        val result = getSurveyProgram(uri).children.map {
            entityModelBuilder(it)
        }
        return CollectionModel.of(result)

    }

    @PutMapping("/surveyprogram/studies/{uri}", produces = ["application/hal+json"])
    @Transactional
    fun putStudies(@PathVariable uri: UUID, @RequestBody study: Study): ResponseEntity<List<EntityModel<Study>>> {
        logger.debug("put studies SurveyProgramController...")
        val result =  repository.findById(uri).orElseThrow()
        result.addChildren(study)
        repository.saveAndFlush(result)
        if (result.children.size > 0)
            return ResponseEntity.ok(
                result.children.map {
                    EntityModel.of(it,Link.of("studies"))
                })
        throw NoSuchElementException("No studies")
    }



    @GetMapping("/pdf/surveyprogram/{uri}/{rev}", produces = [MediaType.APPLICATION_PDF_VALUE])
    fun getPdf(@PathVariable uri: UUID, @PathVariable rev: Int): ByteArray {
        logger.debug("get pdf controller...")
        return super.getPdf("$uri:$rev")
    }

    @GetMapping("/xml/surveyprogram/{uri}/{rev}", produces = [MediaType.APPLICATION_XML_VALUE])
    fun getXml(@PathVariable uri: UUID, @PathVariable rev: Int): ResponseEntity<String> {
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

    private fun pageMetadataBuilder(revisions: Page<RepresentationModel<EntityModel<SurveyProgram>>>): PagedModel.PageMetadata {
        return PagedModel.PageMetadata(revisions.size.toLong(),revisions.pageable.pageNumber.toLong(),revisions.totalElements,revisions.totalPages.toLong())
    }

    private fun entityModelBuilder(it: Study): RepresentationModel<EntityModel<Study>> {
        val baseUri = BasicLinkBuilder.linkToCurrentMapping()

        it.children.size
        it.authors.size
        it.comments.size
        it.instruments.size
        Hibernate.initialize(it.agency)
        Hibernate.initialize(it.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(it)
            .link(Link.of("${baseUri}/study/${it.id}"))
            .embed(it.agency, LinkRelation.of("agency"))
            .embed(it.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(it.comments, LinkRelation.of("comments"))
            .embed(it.authors, LinkRelation.of("authors"))
            .build()
    }

    private fun entityModelBuilder(entity: SurveyProgram): RepresentationModel<EntityModel<SurveyProgram>> {
        entity.children.size
        entity.authors.size
        entity.comments.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of("/api/revisions/surveyprogram/${entity.id}:${entity.version.rev}", "self"))
            .embed(entity.agency,LinkRelation.of("agency"))
            .embed(entity.modifiedBy,LinkRelation.of("modifiedBy"))
            .embed(entity.comments,LinkRelation.of("comments"))
            .embed(entity.authors,LinkRelation.of("authors"))
            .embed(entity.children.map {
                entityModelBuilder(it)
            }, LinkRelation.of("studies"))
            .build()
    }

    private fun entityModelBuilder(rev: Revision<Int, SurveyProgram>): RepresentationModel<EntityModel<SurveyProgram>> {
        rev.entity.version.rev = rev.revisionNumber.get()
        return entityModelBuilder(rev.entity)
    }

    private fun getSurveyProgram(uri: String): SurveyProgram{
        val uriId = UriId.fromAny(uri)

        return if (uriId.rev != null)
            repository.findRevision(uriId.id, uriId.rev!!).orElseThrow().entity
        else
            repository.findById(uriId.id).orElseThrow()
    }

}
