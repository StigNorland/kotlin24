// package no.nsd.qddt.domain.responsedomain.web
// import no.nsd.qddt.domain.AbstractController
// import no.nsd.qddt.classes.exception.RequestAbortedException
// import no.nsd.qddt.classes.xml.XmlDDIFragmentAssembler
// import no.nsd.qddt.domain.responsedomain.ResponseDomain
// import no.nsd.qddt.domain.responsedomain.ResponseDomainService
// import no.nsd.qddt.domain.responsedomain.ResponseKind
// import no.nsd.qddt.domain.responsedomain.json.ResponseDomainJsonEdit
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.data.domain.Page
// import org.springframework.data.domain.Pageable
// import org.springframework.data.web.PagedResourcesAssembler
// import org.springframework.hateoas.EntityModel
// import org.springframework.hateoas.PagedModel
// import org.springframework.http.HttpStatus
// import org.springframework.http.MediaType
// import org.springframework.web.bind.annotation.*
// import javax.validation.ConstraintViolationException
// import java.util.UUID
// /**
// * @author Dag Østgulen Heradstveit
// * @author Stig Norland
// */
// @RestController
// @RequestMapping("/responsedomain")
// class ResponseDomainController @Autowired
// constructor(service:ResponseDomainService):AbstractController() {
//   private val service:ResponseDomainService
//   init{
//     this.service = service
//     // CategoryService categoryService1 = categoryService;
//   }
//   @ResponseStatus(value = HttpStatus.OK)
//   @RequestMapping(value = "{id}", method = RequestMethod.GET)
//   fun get(@PathVariable("id") id:UUID):ResponseDomain {
//     return service.findOne(id)
//   }
//   @ResponseStatus(value = HttpStatus.OK)
//   @RequestMapping(value = "", method = RequestMethod.POST)
//   fun update(@RequestBody responseDomain:ResponseDomain):ResponseDomain {
//     return service.save(responseDomain)
//   }
//   @ResponseStatus(value = HttpStatus.CREATED)
//   @RequestMapping(value = "/create", method = RequestMethod.POST)
//   fun create(@RequestBody responseDomain:ResponseDomain):ResponseDomain {
//     assert(responseDomain != null)
//     responseDomain = service.save(responseDomain)
//     return responseDomain
//   }
//   @ResponseStatus(value = HttpStatus.OK)
//   @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
//   @Throws(RequestAbortedException::class)
//   fun delete(@PathVariable("id") id:UUID) {
//     try
//     {
//       service.delete(id)
//     }
//     catch (cex:ConstraintViolationException) {
//       throw RequestAbortedException(cex)
//     }
//     catch (ex:Exception) {
//       throw RequestAbortedException("This ResponseDomain is referenced and cannot be deleted.")
//     }
//   }
//   @RequestMapping(value = "/page/search", method = RequestMethod.GET, params = { "ResponseKind" }, produces = {MediaType.APPLICATION_JSON_VALUE})
//   fun getBy(@RequestParam("ResponseKind") response:ResponseKind,
//             @RequestParam(value = "description", defaultValue = "") description:String,
//             @RequestParam(value = "question", defaultValue = "") question:String,
//             @RequestParam(value = "name", defaultValue = "") name:String,
//             @RequestParam(value = "anchor", defaultValue = "") anchor:String,
//             @RequestParam(value = "xmlLang", defaultValue = "") xmlLang:String,
//             pageable:Pageable, assembler:PagedResourcesAssembler<ResponseDomainJsonEdit>):PagedModel<EntityModel<ResponseDomainJsonEdit>> {
//     val responseDomains:Page<ResponseDomainJsonEdit> = null
//     try
//     {
//       responseDomains = service.findBy(response, name, description, question, anchor, xmlLang, pageable).map(???({ this.responseDomain2Json(it) }))
//     }
//     catch (ex:Exception) {
//       LOG.error("getBy", ex)
//       throw ex
//     }
//     return assembler.toModel(responseDomains)
//   }
//   @ResponseStatus(value = HttpStatus.OK)
//   @RequestMapping(value = "/xml/{id}", method = RequestMethod.GET)
//   fun getXml(@PathVariable("id") id:UUID):String {
//     return XmlDDIFragmentAssembler<ResponseDomain>(service.findOne(id)).compileToXml()
//   }
//   private fun responseDomain2Json(responseDomain:ResponseDomain):ResponseDomainJsonEdit {
//     return ResponseDomainJsonEdit(responseDomain)
//   }
// }