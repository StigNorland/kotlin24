package no.nsd.qddt.controller

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.repository.SurveyProgramRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.EntityModel
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
    fun getPdf(@PathVariable uri: UUID,@PathVariable rev: Long): ByteArray {
        logger.debug("get pdf controller...")
        return super.getPdf("$uri:$rev")
    }

    @GetMapping("/xml/surveyprogram/{uri}/{rev}", produces = [MediaType.APPLICATION_XML_VALUE])
    fun getXml(@PathVariable uri: UUID,@PathVariable rev: Long): ResponseEntity<String> {
        return  super.getXml("$uri:$rev")
    }

//    @Transactional(propagation = Propagation.REQUIRED)
//    @GetMapping("/revisions/surveyprogram/{uri}", produces = ["application/hal+json"] )
//    override fun getRevisions(@PathVariable uri: String, pageable: Pageable): Page<EntityModel<SurveyProgram>> {
//        val qPage: Pageable = if (pageable.sort.isUnsorted) {
//            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.Direction.DESC,"modified")
//        } else {
//            pageable
//        }
//        logger.debug("getRevisions 1: {}" , qPage)
//
//
//        val result = repository.findRevisions(UriId.fromAny(uri).id, qPage )
//        logger.debug("getRevisions 2: {}" , result.totalElements)
//        val entities = result.content.map {
//            initializeAndUnproxy(it.entity.agency)
//            initializeAndUnproxy(it.entity.modifiedBy)
//            initializeAndUnproxy(it.entity.authors)
//            it.entity.rev = it.revisionNumber.get()
//            EntityModel.of(it.entity)
//        }
//        logger.debug("getRevisions 3: {}" , entities.size)
//        val page: Page<EntityModel<SurveyProgram>> = PageImpl(entities, result.pageable, result.totalElements )
//        result.let { page ->
//            page.map {
//                it.entity.rev = it.revisionNumber.get()
//                EntityModel.of(it.entity)
//            }
//        }
//        return try {
//            page
//        } catch (ex:Exception){
//            logger.error(ex.localizedMessage)
//            Page.empty()
//        }
//    }
//
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
