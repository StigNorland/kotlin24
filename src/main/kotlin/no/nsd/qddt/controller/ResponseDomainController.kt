package no.nsd.qddt.controller

import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.repository.ResponseDomainRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody




@BasePathAwareController

class ResponseDomainController(@Autowired repository: ResponseDomainRepository): AbstractRestController<ResponseDomain>(repository) {


//    @GetMapping("/responsedomain/{uri}",produces = ["application/hal+json"])
//    override fun getById(@PathVariable uri: String): ResponseEntity<EntityModel<ResponseDomain>> {
//        return super.getById(uri).also {
//            it.body?.add(linkTo<ResponseDomainController> { getById(uri) }.withSelfRel())
////            it.body?.add(linkTo<ResponseDomainController> { getById(uri) }.withSelfRel())
////            it.body?.add(linkTo<ResponseDomainController> { getById(uri) }.withSelfRel())
//
////            it.body.content.managedRepresentation.id
////            it.body?.add(linkTo<BaseMixedRepository<Category>> { getById(uri) }.withSelfRel())
//        }
//    }

    @GetMapping("/responsedomain/{uri}/revisions",produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

//    @Throws(IOException::class)
//    fun getFile(): ByteArray? {
//        val `in`: InputStream = javaClass
//            .getResourceAsStream("/com/baeldung/produceimage/data.txt")
//        return IOUtils.toByteArray(`in`)
//    }

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
