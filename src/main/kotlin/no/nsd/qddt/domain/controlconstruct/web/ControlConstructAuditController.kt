package no.nsd.qddt.domain.controlconstruct.web

import no.nsd.qddt.domain.AbstractEntityAudit.ChangeKind
import no.nsd.qddt.domain.controlconstruct.audit.ControlConstructAuditService
import no.nsd.qddt.domain.controlconstruct.pojo.ControlConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author Stig Norland
 */
@RestController
@RequestMapping(value = ["/audit/controlconstruct"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ControlConstructAuditController @Autowired constructor(private val auditService: ControlConstructAuditService) {
    // @JsonView(View.Audit.class)
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.GET])
    fun getLastRevision(@PathVariable("id") id: UUID): Revision<Int?, ControlConstruct?>? {
        return auditService.findLastChange(id)
    }

    // @JsonView(View.Audit.class)
    @RequestMapping(value = ["/{id}/{revision}"], method = [RequestMethod.GET])
    fun getByRevision(
        @PathVariable("id") id: UUID,
        @PathVariable("revision") revision: Int
    ): Revision<Int?, ControlConstruct?>? {
        return auditService.findRevision(id, revision)
    }

    // @JsonView(View.Audit.class)
    @RequestMapping(value = ["/{id}/all"], method = [RequestMethod.GET])
    fun allProjects(
        @PathVariable("id") id: UUID,
        @RequestParam(
            value = "ignorechangekinds",
            defaultValue = "IN_DEVELOPMENT,UPDATED_HIERARCHY_RELATION,UPDATED_PARENT"
        ) changekinds: Collection<ChangeKind?>?,
        pageable: Pageable?, assembler: PagedResourcesAssembler<Revision<Int?, ControlConstruct?>?>
    ): PagedModel<EntityModel<Revision<Int?, ControlConstruct?>?>> {
        return assembler.toModel(
            auditService.findRevisionsByChangeKindNotIn(id, changekinds, pageable)
        )
    }

    @ResponseBody
    @RequestMapping(value = ["/pdf/{id}/{revision}"], method = [RequestMethod.GET], produces = ["application/pdf"])
    fun getPdf(@PathVariable("id") id: UUID, @PathVariable("revision") revision: Int): ByteArray {
        return auditService.findRevision(id, revision)!!.entity.makePdf().toByteArray()
    }
}
