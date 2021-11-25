package no.nsd.qddt.controller

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.repository.StudyRepository
import no.nsd.qddt.repository.SurveyProgramRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

//@RepositoryRestController
@BasePathAwareController
class StudyController(@Autowired repository: StudyRepository): AbstractRestController<Study>(repository) {

//    @GetMapping("/study/{uri}",produces = ["application/hal+json"])
//    override fun getById(@PathVariable uri: String): ResponseEntity<EntityModel<Study>> {
//        return super.getById(uri)
//    }

//    @GetMapping("/study/{uri}/revisions", produces = ["application/hal+json"] )
    @GetMapping("/revisions/study/{uri}", produces =  ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable): RepresentationModel<EntityModel<Study>> {
        return super.getRevisions(uri, pageable)
    }


//    @GetMapping("/study/{uri}/pdf", produces = [MediaType.APPLICATION_PDF_VALUE])
    @GetMapping("/pdf/study/{uri}/{rev}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

//    @GetMapping("/study/{uri}/xml", produces = [MediaType.APPLICATION_XML_VALUE])
    @GetMapping("/xml/study/{uri}/{rev}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return  super.getXml(uri)
    }
}
