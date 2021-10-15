package no.nsd.qddt.controller

import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.repository.SurveyProgramRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.EntityModel
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


//@RepositoryRestController
@BasePathAwareController
class SurveyProgramController(@Autowired repository: SurveyProgramRepository): AbstractRestController<SurveyProgram>(repository) {
// https://docs.spring.io/spring-hateoas/docs/current/reference/html/#fundamentals.representation-models

    @GetMapping("/surveyprogram/{uri}", produces = ["application/hal+json"] )
    override fun getById(@PathVariable uri: String): ResponseEntity<EntityModel<SurveyProgram>> {
        return super.getById(uri)
    }

    @GetMapping("/revisions/{uri}", produces = ["application/hal+json"] )
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable): ResponseEntity<Page<EntityModel<SurveyProgram>>> {
        return super.getRevisions(uri, pageable)
    }


    @GetMapping("/surveyprogram/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        logger.debug("get pdf controller...")
        return super.getPdf(uri)
    }

    @GetMapping("/surveyprogram/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return  super.getXml(uri)
    }
}
