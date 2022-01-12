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
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*

@Transactional(propagation = Propagation.REQUIRED)
@BasePathAwareController
class SurveyProgramController(@Autowired repository: SurveyProgramRepository): AbstractRestController<SurveyProgram>(repository) {
// https://docs.spring.io/spring-hateoas/docs/current/reference/html/#fundamentals.representation-models

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/surveyprogram/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevision(@PathVariable uri: String):RepresentationModel<*> {
        return super.getRevision(uri)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/surveyprogram/revisions/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: UUID, pageable: Pageable):RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

//    @Transactional(propagation = Propagation.REQUIRED)
//    @GetMapping("/surveyprogram/revisions/byparent/{uri}", produces = ["application/hal+json"])
//    fun getStudies(@PathVariable uri: String, pageable: Pageable): RepresentationModel<*>{
//        logger.debug("get Study by parent rev...")
//        return super.getRevisionsByParent(uri,Study::class.java, pageable)
//    }
    @GetMapping("/surveyprogram/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/surveyprogram/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }

//    @Transactional(propagation = Propagation.REQUIRED)
//    @GetMapping("/surveyprogram", produces = ["application/json"])
//    fun getAllByAgency(): ResponseEntity<List<SurveyProgram>> {
//        val user = SecurityContextHolder.getContext().authentication.principal as no.nsd.qddt.model.User
//
//        return ResponseEntity.ok((repository as SurveyProgramRepository).findByAgency(user.agency))
////            .map {entityModelBuilder(it)}
//    }


    @PutMapping("/surveyprogram/{uri}/children", produces = ["application/hal+json"])
    fun putStudies(@PathVariable uri: UUID, @RequestBody study: Study): ResponseEntity<RepresentationModel<EntityModel<Study>>> {
        logger.debug("put studies from SurveyProgramController...")
        val survey =  repository.findById(uri).orElseThrow()
        survey.addChildren(study)
        val studySaved = repository.saveAndFlush(survey).children.last() as Study

        return ResponseEntity.ok(entityModelBuilder(studySaved))

//        throw NoSuchElementException("No studies")
    }
    private fun entityModelBuilder(entity: Study): RepresentationModel<EntityModel<Study>> {
        logger.debug("entityModelBuilder SurveyProgram Study: {}" , entity.id)
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
        val uriId = UriId.fromAny("${entity.id}:${entity.version.rev}")
        logger.debug("entityModelBuilder SurveyProgram : {}" , uriId)
        val baseUrl = if(uriId.rev != null)
            "${baseUri}/surveyprogram/revision/${uriId}"
        else
            "${baseUri}/surveyprogram/${uriId.id}"
        entity.children.size
        entity.authors.size
        entity.comments.size
        entity.comments.forEach {
            logger.debug("initialize(it")
            Hibernate.initialize(it.modifiedBy)
        }
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
        return HalModelBuilder.halModel()
            .entity(entity)
            .link(Link.of(baseUrl))
            .embed(entity.agency,LinkRelation.of("agency"))
            .embed(entity.modifiedBy,LinkRelation.of("modifiedBy"))
            .embed(entity.comments,LinkRelation.of("comments"))
            .embed(entity.authors,LinkRelation.of("authors"))
            .embed(entity.children.map {
                entityModelBuilder(it as Study)
            }, LinkRelation.of("studies"))
            .build()
    }




}
