package no.nsd.qddt.controller

import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.repository.QuestionItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.hateoas.EntityModel
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@RepositoryRestController
class QuestionItemController(@Autowired repository: QuestionItemRepository): AbstractRestController<QuestionItem>(repository) {

    @GetMapping("/questionitem/{uri}",produces = ["application/hal+json"])
    override fun getById(@PathVariable uri: String): ResponseEntity<EntityModel<QuestionItem>> {
        return super.getById(uri)
    }

    @GetMapping("/questionitem/{uri}/revisions", produces = ["application/hal+json"] )
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable): ResponseEntity<Page<EntityModel<QuestionItem>>> {
        return super.getRevisions(uri, pageable)
    }


    @GetMapping("/questionitem/{uri}/pdf", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/questionitem/{uri}/xml", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return  super.getXml(uri)
    }
}
