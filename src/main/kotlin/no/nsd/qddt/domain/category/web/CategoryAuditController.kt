package no.nsd.qddt.domain.category.web

import no.nsd.qddt.domain.AbstractEntityAudit.ChangeKind
import no.nsd.qddt.domain.category.Category
import no.nsd.qddt.domain.category.audit.CategoryAuditService
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
@RequestMapping(value = ["/audit/category"], produces = [MediaType.APPLICATION_JSON_VALUE])
class CategoryAuditController @Autowired constructor(private val auditService: CategoryAuditService) {
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.GET])
    fun getLastRevision(@PathVariable("id") id: UUID): Revision<Int?, Category?>? {
        return auditService.findLastChange(id)
    }

    @RequestMapping(value = ["/{id}/{revision}"], method = [RequestMethod.GET])
    fun getByRevision(
        @PathVariable("id") id: UUID,
        @PathVariable("revision") revision: Int
    ): Revision<Int?, Category?>? {
        return auditService.findRevision(id, revision)
    }

    @RequestMapping(value = ["/{id}/all"], method = [RequestMethod.GET])
    fun allProjects(
        @PathVariable("id") id: UUID,
        @RequestParam(
            value = "ignorechangekinds",
            defaultValue = "IN_DEVELOPMENT,UPDATED_HIERARCHY_RELATION,UPDATED_PARENT"
        ) changekinds: Collection<ChangeKind?>?,
        pageable: Pageable?, assembler: PagedResourcesAssembler<Revision<Int?, Category?>?>
    ): PagedModel<EntityModel<Revision<Int?, Category?>?>> {
        return assembler.toModel(
            auditService.findRevisionByIdAndChangeKindNotIn(id, changekinds, pageable)
        )
    }
}
