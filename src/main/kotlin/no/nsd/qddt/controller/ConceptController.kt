package no.nsd.qddt.controller

import no.nsd.qddt.model.Concept
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.ConceptRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*


@Transactional(propagation = Propagation.REQUIRED)
@BasePathAwareController
class ConceptController(@Autowired repository: ConceptRepository): AbstractRestController<Concept>(repository) {

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/concept/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String):RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/concept/revisions/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: UUID, pageable: Pageable):RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/concept/revisions/byparent/{uri}", produces = ["application/hal+json"])
    fun getParent(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*>{
        logger.debug("get Study by parent rev...")
        return super.getRevisionsByParent(uri, Concept::class.java, pageable)
    }


    @GetMapping("/concept/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/concept/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }


    @Transactional(propagation = Propagation.NESTED)
    @PutMapping("/concept/{uri}/children", produces = ["application/hal+json"])
    fun putStudies(@PathVariable uri: UUID, @RequestBody  concept: Concept):  ResponseEntity<RepresentationModel<EntityModel<Concept>>> {
        logger.debug("put concept TopicController...")

        var parent =  repository.findById(uri).orElseThrow()
        parent.addChildren(concept)
        val conceptSaved = repository.saveAndFlush(parent).children.last() as Concept
        return ResponseEntity.ok(entityModelBuilder(conceptSaved))
    }

    override fun entityModelBuilder(entity: Concept): RepresentationModel<EntityModel<Concept>> {
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        logger.debug("entityModelBuilder ConceptController : {}" , uriId)
        val baseUrl = if(uriId.rev != null)
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
                .entity(entity)
                .link(Link.of("${baseUri}/concept/${entity.id}"))
                .embed(entity.agency, LinkRelation.of("agency"))
                .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
                .embed(entity.comments, LinkRelation.of("comments"))
                .embed(entity.authors, LinkRelation.of("authors"))
                .embed(entity.questionItems, LinkRelation.of("questionItems"))
                .embed(entity.children.map {
                    entityModelBuilder(it as Concept)
                }, LinkRelation.of("children"))
                .build()
        }
}
