package no.nsd.qddt.domain.questionitem.web

import no.nsd.qddt.classes.AbstractEntityAudit
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID
import org.springframework.data.history.Revision
import no.nsd.qddt.domain.questionitem.QuestionItem
import no.nsd.qddt.domain.questionitem.audit.QuestionItemAuditService
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ResponseBody

/**
 * @author Stig Norland
 */
@RestController
@RequestMapping(value = ["/audit/questionitem"],  produces = [MediaType.APPLICATION_JSON_VALUE])
internal class QuestionItemAuditController @Autowired constructor(service: QuestionItemAuditService) {
    private val auditService: QuestionItemAuditService

    // @JsonView(View.Audit.class)
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.GET])
    fun getLastRevision(@PathVariable("id") id: UUID): Revision<Int, QuestionItem>? {
        return auditService.findLastChange(id)
    }

    // @JsonView(View.Audit.class)
    @RequestMapping(value = ["/{id}/{revision}"], method = [RequestMethod.GET])
    fun getByRevision(
        @PathVariable("id") id: UUID,
        @PathVariable("revision") revision: Int
    ): Revision<Int, QuestionItem>? {
        return auditService.findRevision(id, revision)
    }

    @RequestMapping(value = ["/{id}/all"], method = [RequestMethod.GET])
    fun allProjects(@PathVariable("id") id: UUID,
        @RequestParam(value = "ignorechangekinds",
            defaultValue = "IN_DEVELOPMENT,UPDATED_HIERARCHY_RELATION,UPDATED_PARENT"
        ) changekinds: Collection<AbstractEntityAudit.ChangeKind>,
        pageable: Pageable, assembler: PagedResourcesAssembler<Revision<Int, QuestionItem>>
    ): PagedModel<EntityModel<Revision<Int, QuestionItem>>> {
        val entities: Page<Revision<Int, QuestionItem>> =
            auditService.findRevisionByIdAndChangeKindNotIn(id, changekinds, pageable)
        return assembler.toModel(entities)
    }

    @ResponseBody
    @RequestMapping(value = ["/pdf/{id}/{revision}"], method = [RequestMethod.GET], produces = ["application/pdf"])
    fun getPdf(@PathVariable("id") id: UUID, @PathVariable("revision") revision: Int): ByteArray {
        auditService.findRevision(id, revision)?.let {
            return it.entity.makePdf().toByteArray()
        }
        return ByteArray(0)
    }

    init {
        auditService = service
    }
}
