package no.nsd.qddt.controller

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.SurveyProgramRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.*
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
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
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable): Page<EntityModel<SurveyProgram>> {
        val uriId = UriId.fromAny(uri)
        val qPage: Pageable = if (pageable.sort.isUnsorted) {
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.Direction.DESC, "RevisionNumber")
        } else {
            pageable
        }
        val  result =
            if (uriId.rev != null) {
                repository.findRevision(uriId.id, uriId.rev!!).map { rev ->
                    rev.entity.version.rev = rev.revisionNumber.get()
                    val item = EntityModel.of<SurveyProgram>(
                        rev.entity,
                        Link.of("/api/revisions/surveyprogram/${rev.entity.id}:${rev.entity.version.rev}", "self")
                    )
                    val page = PageImpl(mutableListOf(item), pageable, 1)
                    page
                }.orElseThrow()
            } else {
                repository.findRevisions(uriId.id, qPage).map {
                    it.entity.version.rev = it.revisionNumber.get()
                    EntityModel.of<SurveyProgram>(
                        it.entity,
                        Link.of("/api/revisions/surveyprogram/${it.entity.id}:${it.entity.version.rev}", "self")
                    )
                }
            }
        return try {
          result!!.map {
            it.content?.children?.size
            it.content?.authors?.size
            Hibernate.initialize(it.content?.agency)
            Hibernate.initialize(it.content?.modifiedBy)
            it
          }

        } catch (ex: Exception) {
            with(logger) { error(ex.localizedMessage) }
            Page.empty()
        }
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
