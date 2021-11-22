//package no.nsd.qddt.controller
//
//import no.nsd.qddt.model.SurveyProgram
//import no.nsd.qddt.model.classes.UriId
//import no.nsd.qddt.model.enums.ElementKind
//import no.nsd.qddt.service.RevisionService
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.Pageable
//import org.springframework.data.history.Revision
//import org.springframework.data.rest.webmvc.BasePathAwareController
//import org.springframework.data.rest.webmvc.RepositoryRestController
//import org.springframework.hateoas.EntityModel
//import org.springframework.transaction.annotation.Propagation
//import org.springframework.transaction.annotation.Transactional
//import org.springframework.web.bind.annotation.*
//import java.util.*
//
//
//@BasePathAwareController
//class RevisionController(val revisionService: RevisionService) {
//    internal val logger: Logger = LoggerFactory.getLogger(this.javaClass)
//
//    @GetMapping("/revisions/surveyprogram/{uri}", produces = ["application/hal+json"] )
//    fun getRevisions(@PathVariable uri: String, pageable: Pageable): Page<Revision<Int, SurveyProgram>> {
//        return revisionService
//            .getRevisions(
//                UriId.fromAny(uri)
//                ,ElementKind.SURVEY_PROGRAM
//                ,pageable)
//    }
//
//    @GetMapping("/revisions/surveyprogram/{uuid}/{rev}", produces = ["application/hal+json"] )
//    @ResponseBody
//    fun getRevisions(@PathVariable uuid: UUID, @PathVariable rev:Long ): Revision<Int, SurveyProgram>? {
//        val result =  revisionService.getRevision<SurveyProgram>(UriId.fromAny("${uuid}:${rev}"),ElementKind.SURVEY_PROGRAM)
//        return  result
//    }
//
//
//}
