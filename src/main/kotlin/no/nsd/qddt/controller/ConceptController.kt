package no.nsd.qddt.controller

import no.nsd.qddt.model.Concept
import no.nsd.qddt.model.Study
import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.repository.ConceptRepository
import no.nsd.qddt.repository.StudyRepository
import no.nsd.qddt.repository.TopicGroupRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageImpl
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

    @GetMapping("/concept/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

    @GetMapping("/pdf/concept/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/xml/concept/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/concept/revision/byparent/{uri}", produces = ["application/hal+json"])
    fun getStudies(@PathVariable uri: String): RepresentationModel<*> {
        logger.debug("get Study by parent rev...")
        return super.getRevisionByParent(uri, Concept::class.java)
    }

    @GetMapping("/concept/children/{uri}", produces = ["application/prs.hal-forms+json"])
    fun getConcept(@PathVariable uri: String): RepresentationModel<*> {
        logger.debug("get studies SurveyProgramController...")
        val result = getByUri(uri).children.map {
            entityModelBuilder(it as Concept)
        }
        return CollectionModel.of(result)

    }

    @PutMapping("/concept/children/{uri}", produces = ["application/hal+json"])
    fun putConcept(@PathVariable uri: UUID, @RequestBody concept: Concept): ResponseEntity<List<EntityModel<Concept>>> {
        logger.debug("put concept ConceptController...")
        val result = repository.findById(uri).orElseThrow()
        result.addChildren(concept)
        repository.saveAndFlush(result)
        if (result.children.size > 0)
            return ResponseEntity.ok(
                result.children.map {
                    EntityModel.of(it as Concept, Link.of("concepts"))
                })
        throw NoSuchElementException("No concepts")
    }

    override fun entityModelBuilder(entity: Concept): RepresentationModel<EntityModel<Concept>> {
        entity.children.size
        entity.authors.size
        entity.comments.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of("${baseUri}/concept/${entity.id}"))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.authors, LinkRelation.of("authors"))
            .build()
    }
}
