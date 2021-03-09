package no.nsd.qddt.controller

import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.repository.ResponseDomainRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.io.Serializable

@BasePathAwareController
class ResponseDomainController(@Autowired repository: ResponseDomainRepository): AbstractRestController<ResponseDomain>(repository) {


    @GetMapping("/responsedomain/{uri}",produces = ["application/hal+json"])
    override fun getById(@PathVariable uri: String): ResponseEntity<ResponseDomain> {
        return super.getById(uri)
    }

    @GetMapping("/responsedomain/revisions/{uri}",produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable): ResponseEntity<Page<Revision<Int, ResponseDomain>>> {
        return super.getRevisions(uri, pageable)
    }


    @GetMapping("/responsedomain/{uri}/pdf", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray? {
        return super.getPdf(uri)
    }

    @GetMapping("/responsedomain/{uri}/xml", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): String {
        return  super.getXml(uri)
    }
}