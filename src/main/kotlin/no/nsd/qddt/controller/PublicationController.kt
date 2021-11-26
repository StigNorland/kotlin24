package no.nsd.qddt.controller

import no.nsd.qddt.model.Publication
import no.nsd.qddt.model.PublicationStatus
import no.nsd.qddt.model.User
import no.nsd.qddt.repository.PublicationRepository
import no.nsd.qddt.repository.PublicationStatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.*
import org.springframework.data.history.Revision
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.annotation.CurrentSecurityContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.security.Principal

@BasePathAwareController
class PublicationController(@Autowired repository: PublicationRepository): AbstractRestController<Publication>(repository) {

    @Autowired
    lateinit var publicationStatusRepository: PublicationStatusRepository

    @GetMapping("/publication/status",produces = ["application/json"])
    fun getHierarchy(): ResponseEntity<MutableList<PublicationStatus>> {
        val args = mutableListOf<Long>(0, 1, 2)

        return ResponseEntity.ok(publicationStatusRepository.findAllById(args))
    }

    @GetMapping("/publication/status/flat",produces = ["application/json"])
    fun getAllStatus(): ResponseEntity<MutableList<PublicationStatus>> {
        return ResponseEntity.ok(publicationStatusRepository.findAll(Sort.by("id")))
    }


    @GetMapping("/publication/test",produces = ["application/hal+json"])
    fun getAllByTest(user: Principal,
                     pageable: Pageable?): ResponseEntity<EntityModel<Page<Publication>>> {
        val details =  user as UsernamePasswordAuthenticationToken;
        logger.debug(details.principal.toString());
        val page = repository.findAll(pageable?: Pageable.unpaged())
        return ResponseEntity.ok(EntityModel.of(page))
    }

//    @GetMapping("/publication",produces = ["application/hal+json"])
//    fun getAllBy(@CurrentSecurityContext(expression = "authentication") authentication: Authentication,
//        pageable: Pageable?): ResponseEntity<EntityModel<Page<Publication>>> {
//            val details =  authentication.principal as User
//            val page = repository.findAll(pageable?: Pageable.unpaged())
//            return ResponseEntity.ok(EntityModel.of(page))
//    }

//    @GetMapping("/publication/{uri}",produces = ["application/hal+json"])
//    override fun getById(@PathVariable uri: String): ResponseEntity<EntityModel<Publication>> {
//        return super.getById(uri)
//    }

    @GetMapping("/publication/{uri}/revisions", produces = ["application/hal+json"] )
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable): RepresentationModel<EntityModel<Publication>> {
        return super.getRevisions(uri, pageable)
    }


    @GetMapping("/publication/{uri}/pdf", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/publication/{uri}/xml", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return  super.getXml(uri)
    }
}
