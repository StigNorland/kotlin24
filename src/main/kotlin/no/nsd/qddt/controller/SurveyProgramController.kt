package no.nsd.qddt.controller

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.interfaces.IBasedOn
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

@Transactional(propagation = Propagation.REQUIRED)
@BasePathAwareController
class SurveyProgramController(@Autowired repository: SurveyProgramRepository): AbstractRestController<SurveyProgram>(repository) {
// https://docs.spring.io/spring-hateoas/docs/current/reference/html/#fundamentals.representation-models

    @GetMapping("/surveyprogram/revision/{uri}", produces = ["application/hal+json"])
    override fun getRevisions(@PathVariable uri: String, pageable: Pageable):RepresentationModel<*> {
        return super.getRevisions(uri, pageable)
    }

    @GetMapping("/surveyprogram/pdf/{uri}", produces = [MediaType.APPLICATION_PDF_VALUE])
    override fun getPdf(@PathVariable uri: String): ByteArray {
        return super.getPdf(uri)
    }

    @GetMapping("/surveyprogram/xml/{uri}", produces = [MediaType.APPLICATION_XML_VALUE])
    override fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        return super.getXml(uri)
    }



    @GetMapping("/surveyprogram/studies/{uri}", produces = ["application/hal+json"])
    fun getStudies(@PathVariable uri: String): ResponseEntity<List<RepresentationModel<EntityModel<Study>>>> {
        logger.debug("get studies from SurveyProgramController...")

        val result = super.getByUri(uri).children.map {
            entityModelBuilder(it as Study)
//            EntityModel.of(it,Link.of("studies"))
        }
        return  ResponseEntity.ok().body(result)
//        return CollectionModel.of(result)

    }

    @PutMapping("/surveyprogram/{uri}/children", produces = ["application/hal+json"])
    fun putStudies(@PathVariable uri: UUID, @RequestBody study: Study): RepresentationModel<*> {
        logger.debug("put studies from SurveyProgramController...")
        val result =  repository.findById(uri).orElseThrow()
        result.children.add(study.apply {
            changeKind = IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
            changeComment = String.format("{} [ {} ] added", study.classKind, study.name)
        })
        repository.saveAndFlush(result)
        if (result.children.size > 0)
            return CollectionModel.of(result)
//            return ResponseEntity.ok(
//                result.children.map {
//                    entityModelBuilder(it)
//                })
        throw NoSuchElementException("No studies")
    }
    private fun entityModelBuilder(entity: Study): RepresentationModel<EntityModel<Study>> {
        logger.debug("entityModelBuilder SurveyProgram Study: {}" , entity.id)
//        entity.children.size
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
