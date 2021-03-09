//package no.nsd.qddt.controller
//
//import no.nsd.qddt.model.Study
//import no.nsd.qddt.model.SurveyProgram
//import no.nsd.qddt.model.User
//import no.nsd.qddt.model.interfaces.IBasedOn
//import no.nsd.qddt.repository.SurveyProgramRepository
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.domain.Pageable
//import org.springframework.data.rest.webmvc.RepositoryRestController
//import org.springframework.http.MediaType
//import org.springframework.security.core.annotation.AuthenticationPrincipal
//import org.springframework.web.bind.annotation.*
//import java.util.*
//import java.util.function.Consumer
//import javax.persistence.EntityNotFoundException
//
///**
// * @author Stig Norland
// */
//@RepositoryRestController
//@RequestMapping(path = ["/surveyprogram"])
//class SurveyProgramController {
//
//    @Autowired
//    lateinit var repository: SurveyProgramRepository
//
//
//    @GetMapping(value = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
//    operator fun get(@PathVariable("id") id: UUID): SurveyProgram {
//        return repository.findLastChangeRevision(id).get().entity
//    }
//
//    @GetMapping(value = ["/{id}/{rev}"], produces = [MediaType.APPLICATION_JSON_VALUE])
//    operator fun get(@PathVariable("id") id: UUID, @PathVariable("rev") rev: Int?): SurveyProgram {
//        if (rev != null)
//            return repository.findRevision(id,rev).get().entity
//
//        return repository.findLastChangeRevision(id).get().entity
//    }
//
//
//    @PostMapping(value = ["/"], produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun update(@RequestBody instance: SurveyProgram): SurveyProgram? {
//        instance.children.forEach {
//            it.changeKind = IBasedOn.ChangeKind.UPDATED_PARENT
//            it.changeComment = "touched me to stay in sync..."
//        }
//        return repository.save(instance)
//    }
//
//    @PostMapping(value = ["/create"], produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun create(@RequestBody instance: SurveyProgram): SurveyProgram {
//        return repository.save(instance)
//    }
//
//
//    @DeleteMapping(value = ["/delete/{id}"])
//    fun delete(@PathVariable("id") id: UUID) {
//        repository.deleteById(id)
//    }
//
//
//    @GetMapping(value = ["/list/"] )
//    fun listByUser(@AuthenticationPrincipal user:User?) {
//        if (user == null)
//            throw EntityNotFoundException("User not found")
////        val agent = user?.agency ?: throw EntityNotFoundException("by agent")
//        user.agencyId?.let { repository.findByAgency_Id(it, Pageable.unpaged()) }
//    }
//
//
//    @ResponseBody
//    @GetMapping(value = ["/pdf/{id}/{rev}"], produces = ["application/pdf"])
//    fun getPdf(@PathVariable("id") id: UUID, @PathVariable("rev") rev: Int?): ByteArray? {
//        if (rev != null)
//            return repository.findRevision(id, rev).get().entity.makePdf().toByteArray()
//
//        return repository.findById(id).get().makePdf().toByteArray()
//    }
//
//    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)
//
//}
