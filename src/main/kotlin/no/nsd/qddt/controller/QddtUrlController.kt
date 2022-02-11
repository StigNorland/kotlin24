//package no.nsd.qddt.controller
//
//import no.nsd.qddt.model.views.QddtUrl
//import no.nsd.qddt.service.SearchService
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.http.HttpStatus
//import org.springframework.web.bind.annotation.*
//import org.springframework.web.servlet.mvc.AbstractController
//import java.util.*
//
///**
// * @author Stig Norland
// */
//@RestController
//@RequestMapping("/preview")
//class QddtUrlController(@Autowired service: SearchService) : AbstractController() {
//
//    @ResponseStatus(value = HttpStatus.OK)
//    @GetMapping
//    operator fun get(@PathVariable("id") id: UUID): QddtUrl {
//        return service.findPath(id)
//    }
//
//    @ResponseStatus(value = HttpStatus.OK)
//    @RequestMapping(value = ["/{id}/{revision}"], method = [RequestMethod.GET])
//    fun getByRevision(@PathVariable("id") id: UUID?, @PathVariable("revision") revision: Int?): QddtUrl {
//        val url: QddtUrl = service.findPath(id)
//        url.setRevision(revision)
//        return url
//    }
//
//    init {
//        this.service = service
//    }
//}