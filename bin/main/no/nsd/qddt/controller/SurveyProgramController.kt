package no.nsd.qddt.controller

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.model.classes.ElementOrder
import no.nsd.qddt.repository.SurveyProgramRepository
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.*
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*

@Transactional(propagation = Propagation.REQUIRED)
@BasePathAwareController
class SurveyProgramController(@Autowired repository: SurveyProgramRepository) :
    AbstractRestController<SurveyProgram>(repository) {
// https://docs.spring.io/spring-hateoas/docs/current/reference/html/#fundamentals.representation-models

    @GetMapping("/surveyprogram/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String): RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @GetMapping("/surveyprogram/revisions/{uuid}", produces = ["application/hal+json"])
    override fun getRevisions(
        @PathVariable uuid: UUID,
        pageable: Pageable
    ): RepresentationModel<*>? {
        return super.getRevisions(uuid, pageable)
    }


    @GetMapping("/surveyprogram/pdf/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ResponseEntity<ByteArrayResource> {
        return super.getPdf(uri)
    }

    @GetMapping("/surveyprogram/xml/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }


    @PutMapping("/surveyprogram/reorder", produces = ["application/hal+json"])
    fun setOrder( @RequestBody ranks: List<ElementOrder>): ResponseEntity<SurveyProgram> {
//        if (uuid!=null) {
            repository.saveAllAndFlush(
             repository.findAllById(ranks.map { it.uuid })
                .map { survey ->
                        survey.parentIdx = ranks.find { it.uuid == survey.id }?.index
                        survey
                })
//        } else {
//        }
        return ResponseEntity.ok().build()
    }

    @PutMapping("/surveyprogram/{uri}/children", produces = ["application/hal+json"])
    fun putStudies(
        @PathVariable uri: UUID,
        @RequestBody study: Study
    ): ResponseEntity<RepresentationModel<EntityModel<Study>>> {
        logger.debug("put studies from SurveyProgramController...")
        val survey = repository.findById(uri).orElseThrow()
        survey.childrenAdd(study)
        val studySaved = repository.saveAndFlush(survey).children.last() as Study

        return ResponseEntity.ok(entityModelBuilder(studySaved))

//        throw NoSuchElementException("No studies")
    }

    private fun entityModelBuilder(entity: Study): RepresentationModel<EntityModel<Study>> {
        logger.debug("ModelBuilder SurveyProgram Study: {}", entity.id)
//        entity.children.size
        entity.authors.size
        entity.comments.size
        entity.instruments.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of("${baseUri}/study/${entity.id}"))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.authors, LinkRelation.of("authors"))
            .build()
    }

    override fun entityModelBuilder(entity: SurveyProgram): RepresentationModel<EntityModel<SurveyProgram>> {
        val uriId = toUriId(entity)
        val baseUrl = baseUrl(uriId,"surveyprogram")
        logger.debug("ModelBuilder SurveyProgram : {}", uriId)

        entity.children.size
        entity.authors.size
        entity.comments.size
//        entity.comments.forEach {
//            logger.debug("initialize(comments.modifiedBy)")
//            Hibernate.initialize(it.modifiedBy)
//        }
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency!!, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.authors, LinkRelation.of("authors"))
            .embed(entity.children.map {
                entityModelBuilder(it as Study)
            }, LinkRelation.of("children"))
            .build()
    }


}
