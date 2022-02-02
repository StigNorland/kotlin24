package no.nsd.qddt.controller

import no.nsd.qddt.model.Concept
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.embedded.ElementRefQuestionItem
import no.nsd.qddt.model.enums.ElementKind
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
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*


@Transactional(propagation = Propagation.REQUIRED)
@BasePathAwareController
class ConceptController(@Autowired repository: ConceptRepository) : AbstractRestController<Concept>(repository) {

    //    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/concept/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    //    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/concept/revisions/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: UUID, pageable: Pageable): RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

    //    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/concept/revisions/byparent/{uri}", produces = ["application/hal+json"])
    fun getParent(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*> {
        return super.getRevisionsByParent(uri, Concept::class.java, pageable)
    }


    @GetMapping("/concept/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @ResponseBody
    @GetMapping("/concept/{uri}", produces = [MediaType.TEXT_XML_VALUE])
    fun getXmlString(@PathVariable uri: String): ResponseEntity<String> {
//        return super.getXml(uri)
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_XML)
            .body(super.getXml(uri))
    }

    @GetMapping(path = ["/concept/xml/{uri}"], produces = [MediaType.TEXT_XML_VALUE])
    fun download(): ResponseEntity<String> {
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_XML)
            .body(
                """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Student>
    <age>32</age>
    <name>John</name>
</Student>"""
            )
    }

    @Transactional(propagation = Propagation.NESTED)
    @PutMapping("/concept/{uuid}/questionitems", produces = ["application/hal+json"])
    fun addQuestionItem(
        @PathVariable uuid: UUID,
        @RequestBody questionItem: ElementRefQuestionItem
    ): ResponseEntity<MutableList<ElementRefQuestionItem>> {

        val qRepository = repLoaderService.getRepository<QuestionItem>(ElementKind.QUESTION_ITEM)
        repository.findById(uuid).orElseThrow().let { parent ->

            parent.addQuestionItem(questionItem)

            val result = repository.saveAndFlush(parent).questionItems.map {
                it.element = loadRevisionEntity(it.getUri(), qRepository)
                it
            }.toMutableList()

            return ResponseEntity.ok(result)
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    @DeleteMapping("/concept/{uuid}/questionitems", produces = ["application/hal+json"])
    fun removeQuestionItem(
        @PathVariable uuid: UUID,
        @RequestBody questionItem: ElementRefQuestionItem
    ): ResponseEntity<MutableList<ElementRefQuestionItem>> {
        repository.findById(uuid).orElseThrow().let { parent ->
            parent.removeQuestionItem(questionItem)
            return ResponseEntity.ok(
                repository.saveAndFlush(parent).questionItems
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
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        val baseUrl = if (uriId.rev != null)
            "${baseUri}/concept/revision/${uriId}"
        else
            "${baseUri}/concept/${uriId.id}"

        entity.children.size
        entity.authors.size
        entity.comments.size
        entity.questionItems.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity).link(Link.of(baseUrl))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.authors, LinkRelation.of("authors"))
//                .embed(entity.questionItems, LinkRelation.of("questionItems"))
            .embed(entity.children.map {
                entityModelBuilder(it as Concept)
            }, LinkRelation.of("children"))
            .build()
    }
}
