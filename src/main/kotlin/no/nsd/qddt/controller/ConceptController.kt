package no.nsd.qddt.controller

import no.nsd.qddt.model.Concept
import no.nsd.qddt.model.builder.ConceptFactory
import no.nsd.qddt.model.embedded.ElementRefQuestionItem
import no.nsd.qddt.model.embedded.UriId
import no.nsd.qddt.repository.ConceptRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.io.IOException
import java.io.InputStream
import java.util.*


@Transactional(propagation = Propagation.REQUIRED)
@BasePathAwareController
class ConceptController(@Autowired repository: ConceptRepository) : AbstractRestController<Concept>(repository) {

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/concept/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }
    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/concept/revisions/{uuid}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uuid: UUID,pageable: Pageable): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
    }

    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @GetMapping("/concept/revisions/byparent/{uri}", produces = ["application/hal+json"])
    fun getRevisionByParent(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*> {
        return super.getRevisionsByParent(uri, Concept::class.java, pageable)
    }

    @GetMapping("/concept/pdf/{uri}",produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadFile(@PathVariable uri: String): ResponseEntity<ByteArray>
    {
//        val headers = HttpHeaders()
//        headers.contentType = MediaType.APPLICATION_PDF
//        val media = getByUri(uri).makePdf().toByteArray()
//        headers.cacheControl = CacheControl.noCache().headerValue
//
//        return ResponseEntity<ByteArray>(media, headers, HttpStatus.OK)
//        return super.getPdf(uri)
        val pdf = getByUri(uri).makePdf().toByteArray()
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "test.pdf")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(pdf.size.toLong())
            .body(pdf)
    }

    @GetMapping(value = ["/concept/image"],produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE] )
    @Throws(IOException::class)
    fun getImage(): ByteArray? {
        val stream: InputStream = ClassLoader.getSystemResource("qddt.png").openStream()
        return stream.readAllBytes()
    }


    @ResponseBody
    @GetMapping("/concept/xml/{uri}")
    fun getXmlString(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }


    @Transactional(propagation = Propagation.NESTED)
    @ResponseBody
    @PutMapping("/concept/{uuid}/questionitems", produces = ["application/hal+json"])
    fun addQuestionItem(
        @PathVariable uuid: UUID,
        @RequestBody questionItem: ElementRefQuestionItem
    ): ResponseEntity<MutableList<ElementRefQuestionItem>> {

        var parent = repository.findById(uuid).orElseThrow()
        parent.addQuestionRef(questionItem)
        return ResponseEntity.ok(
            repository.saveAndFlush(parent).questionItems
        )
    }

    @Transactional(propagation = Propagation.NESTED)
    @DeleteMapping("/concept/{uuid}/questionitems/{uri}", produces = ["application/hal+json"])
    fun removeQuestionItem(
        @PathVariable uuid: UUID,
        @PathVariable uri: String
    ): ResponseEntity<MutableList<ElementRefQuestionItem>> {

        val parent = repository.findById(uuid).orElseThrow()
        val qUri = UriId.fromAny(uri)

        val qef = parent.questionItems.find { it.uri == qUri }
        if (qef != null) {
            parent.removeQuestionRef(qef)
        }

        return ResponseEntity.ok(
            repository.saveAndFlush(parent).questionItems
        )
    }

    @ResponseBody
    @Transactional(propagation = Propagation.NESTED)
    @PostMapping("/concept/{uuid}/addcopy/{uri}", produces = ["application/hal+json"])
    fun addCopy(@PathVariable uuid: UUID,@PathVariable uri: String): ResponseEntity<RepresentationModel<EntityModel<Concept>>> {

        val uriId = UriId.fromAny(uri)

        repository.findById(uuid).orElseThrow().let { parent ->
            val basedonConcept = repository.findRevision(uriId.id!!, uriId.rev!! ).orElseThrow().let {
                ConceptFactory().copy(it.entity, uriId.rev)
            }
            parent.childrenAdd(basedonConcept)
            return ResponseEntity.ok(
                entityModelBuilder(repository.saveAndFlush(parent).children.last() as Concept)
            )
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    @PutMapping("/concept/{uri}/children", produces = ["application/hal+json"])
    fun addConcept(
        @PathVariable uri: UUID,
        @RequestBody concept: Concept
    ): ResponseEntity<RepresentationModel<EntityModel<Concept>>> {

        repository.findById(uri).orElseThrow().let { parent ->

            parent.childrenAdd(concept)

            return ResponseEntity.ok(
                entityModelBuilder(repository.saveAndFlush(parent).children.last() as Concept)
            )
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    @DeleteMapping("/concept/{uuid}/children", produces = ["application/hal+json"])
    fun removeQuestionItem(
        @PathVariable uuid: UUID,
        @RequestBody concept: Concept
    ): ResponseEntity<List<RepresentationModel<EntityModel<Concept>>>> {
        repository.findById(uuid).orElseThrow().let { parent ->

            parent.childrenRemove(concept)

            return ResponseEntity.ok(
                repository.saveAndFlush(parent).children.map { entityModelBuilder(it as Concept) }
            )
        }
    }


    override fun entityModelBuilder(entity: Concept): RepresentationModel<EntityModel<Concept>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"concept")
        logger.debug("ModelBuilder Concept : {}", uriId)

        entity.children.size
        entity.authors.size
        entity.comments.size
        entity.questionItems.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
//            .link(Link.of(self))
//            .link(Link.of("${baseUrl}/questionItems", "questionItems"))

            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.authors, LinkRelation.of("authors"))
            .embed(entity.children.map {
                entityModelBuilder(it as Concept)
            }, LinkRelation.of("children"))
            .build()
    }
}
