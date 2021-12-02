package no.nsd.qddt.controller

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.SurveyProgramRepository
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


@BasePathAwareController
class SurveyProgramController(@Autowired repository: SurveyProgramRepository): AbstractRestController<SurveyProgram>(repository) {
// https://docs.spring.io/spring-hateoas/docs/current/reference/html/#fundamentals.representation-models

//    @GetMapping("/surveyprogram/{uri}/{rev}", produces = ["application/hal+json"] )
//    fun getById(@PathVariable uri: UUID,@PathVariable rev: Int ): ResponseEntity<EntityModel<SurveyProgram>> {
//        return super.getById("$uri:$rev")
//    }

    @GetMapping("/surveyprogram/study/{uri}", produces = ["application/prs.hal-forms+json"])
    @Transactional
    fun getStudies(@PathVariable uri: String): RepresentationModel<*> {
        logger.debug("get studies SurveyProgramController...")
        val result = super.getByUri(uri).children.map {
            entityModelBuilder(it)
        }
        return CollectionModel.of(result)

    }

    @PutMapping("/surveyprogram/study/{uri}", produces = ["application/hal+json"])
    @Transactional
    fun putStudies(@PathVariable uri: UUID, @RequestBody study: Study): ResponseEntity<List<EntityModel<Study>>> {
        logger.debug("put studies SurveyProgramController...")
        val result =  repository.findById(uri).orElseThrow()
        result.addChildren(study)
        repository.saveAndFlush(result)
        if (result.children.size > 0)
            return ResponseEntity.ok(
                result.children.map {
                    EntityModel.of(it,Link.of("studies"))
                })
        throw NoSuchElementException("No studies")
    }

    @GetMapping("/surveyprogram/pdf/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        logger.debug("get pdf controller...")
        return super.getPdf(uri)
    }

    @GetMapping("/surveyprogram/xml/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/surveyprogram/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*> {
      return super.getRevisions(uri, pageable)
    }


    private fun entityModelBuilder(entity: Study): RepresentationModel<EntityModel<Study>> {

        entity.children.size
        entity.authors.size
        entity.comments.size
        entity.instruments.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of("${baseUri}/study/${entity.id}"))
            .embed(entity.agency, LinkRelation.of("agency"))
            .embed(entity.modifiedBy, LinkRelation.of("modifiedBy"))
            .embed(entity.comments, LinkRelation.of("comments"))
            .embed(entity.authors, LinkRelation.of("authors"))
            .build()
    }

    override fun entityModelBuilder(entity: SurveyProgram): RepresentationModel<EntityModel<SurveyProgram>> {
        entity.children.size
        entity.authors.size
        entity.comments.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of("${baseUri}/surveyprorgam/${entity.id}", "self"))
            .embed(entity.agency,LinkRelation.of("agency"))
            .embed(entity.modifiedBy,LinkRelation.of("modifiedBy"))
            .embed(entity.comments,LinkRelation.of("comments"))
            .embed(entity.authors,LinkRelation.of("authors"))
            .embed(entity.children.map {
                entityModelBuilder(it)
            }, LinkRelation.of("studies"))
            .build()
    }




}
