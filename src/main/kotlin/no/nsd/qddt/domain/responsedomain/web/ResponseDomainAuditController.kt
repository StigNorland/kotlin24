// package no.nsd.qddt.domain.responsedomain.web
// import no.nsd.qddt.domain.AbstractEntityAudit
// import no.nsd.qddt.domain.responsedomain.ResponseDomain
// import no.nsd.qddt.domain.responsedomain.audit.ResponseDomainAuditService
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.data.domain.Page
// import org.springframework.data.domain.PageRequest
// import org.springframework.data.domain.Pageable
// import org.springframework.data.domain.Sort
// import org.springframework.data.history.Revision
// import org.springframework.data.web.PagedResourcesAssembler
// import org.springframework.hateoas.EntityModel
// import org.springframework.hateoas.PagedModel
// import org.springframework.http.MediaType
// import org.springframework.web.bind.annotation.*
// import java.util.Arrays
// import java.util.UUID
// import no.nsd.qddt.domain.AbstractEntityAudit.ChangeKind.*
// /**
// * @author Stig Norland
// */
// @RestController
// @RequestMapping(value = "/audit/responsedomain", produces = MediaType.APPLICATION_JSON_VALUE)
// class ResponseDomainAuditController @Autowired
// constructor(service:ResponseDomainAuditService) {
//   private val auditService:ResponseDomainAuditService
//   init{
//     this.auditService = service
//   }

//   @RequestMapping(value = "/{id}", method = RequestMethod.GET)
//   fun getLastRevision(@PathVariable("id") id:UUID):Revision<Int, ResponseDomain> {
//     return auditService.findLastChange(id)
//   }

//   @RequestMapping(value = "/{id}/{revision}", method = RequestMethod.GET)
//   fun getByRevision(@PathVariable("id") id:UUID, @PathVariable("revision") revision:Int):Revision<Int, ResponseDomain> {
//     return auditService.findRevision(id, revision)
//   }

//   @RequestMapping(value = "/{id}/all", method = RequestMethod.GET)
//   fun allProjects(
//     @PathVariable("id") id:UUID,
//     @RequestParam(value = "ignorechangekinds", defaultValue = "IN_DEVELOPMENT,UPDATED_HIERARCHY_RELATION,UPDATED_PARENT")
//     changekinds:Collection<AbstractEntityAudit.ChangeKind>,
//     pageable:Pageable, assembler:PagedResourcesAssembler<Revision<Int, ResponseDomain>>):PagedModel<EntityModel<Revision<Int, ResponseDomain>>> {
//     val revisions = auditService.findRevisionByIdAndChangeKindNotIn(id, changekinds, pageable)
//     return assembler.toModel(revisions)
//   }

//   @RequestMapping(value = "/{id}/latestversion", method = RequestMethod.GET)
//   fun getLatestVersion(@PathVariable("id") id:UUID):Revision<Int, ResponseDomain> {
//     val changekinds = Arrays.asList<AbstractEntityAudit.ChangeKind>(IN_DEVELOPMENT, UPDATED_HIERARCHY_RELATION, UPDATED_PARENT, UPDATED_CHILD)
//     val pageable = PageRequest.of(0,
//                                   1,
//                                   Sort.by(Sort.Order(Sort.Direction.ASC, "updated")))
//     val revisions = auditService.findRevisionByIdAndChangeKindNotIn(id, changekinds, pageable)
//     return revisions.getContent().get(0)
//   }
// }