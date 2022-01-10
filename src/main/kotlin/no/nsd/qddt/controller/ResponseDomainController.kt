package no.nsd.qddt.controller

import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.repository.ResponseDomainRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*


@BasePathAwareController

class ResponseDomainController(@Autowired repository: ResponseDomainRepository): AbstractRestController<ResponseDomain>(repository) {


    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/responsedomain/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String):RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/responsedomain/revisions/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: UUID, pageable: Pageable):RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

    @GetMapping("/responsedomain/{uri}/pdf", produces = ["application/pdf"])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        logger.debug("PDF : {}", uri)
        return super.getPdf(uri)
    }

    @GetMapping("/responsedomain/{uri}/xml", produces = ["application/xml"])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return  super.getXml(uri)
    }
}
