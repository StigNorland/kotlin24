package no.nsd.qddt.domain.questionitem.web

import no.nsd.qddt.classes.AbstractEntityAudit
import no.nsd.qddt.domain.questionitem.QuestionItem
import no.nsd.qddt.domain.questionitem.audit.QuestionItemAuditRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
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
@RequestMapping(value = ["/audit/questionitem"],  produces = [MediaType.APPLICATION_JSON_VALUE])
internal class QuestionItemAuditController {

    @Autowired
    private lateinit var auditRepository: QuestionItemAuditRepository

    // @JsonView(View.Audit.class)
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.GET])
    fun getLastRevision(@PathVariable("id") id: UUID): Optional<Revision<Int, QuestionItem>> {
        return auditRepository.findLastChangeRevision(id)
    }

    // @JsonView(View.Audit.class)
    @RequestMapping(value = ["/{id}/{revision}"], method = [RequestMethod.GET])
    fun getByRevision(
        @PathVariable("id") id: UUID,
        @PathVariable("revision") revision: Int
    ): Optional<Revision<Int, QuestionItem>> {
        return auditRepository.findRevision(id, revision)
    }

    @RequestMapping(value = ["/{id}/all"], method = [RequestMethod.GET])
    fun allProjects(@PathVariable("id") id: UUID,
        @RequestParam(value = "ignorechangekinds",
            defaultValue = "IN_DEVELOPMENT,UPDATED_HIERARCHY_RELATION,UPDATED_PARENT"
        ) changekinds: Collection<AbstractEntityAudit.ChangeKind>,
        pageable: Pageable, assembler: PagedResourcesAssembler<Revision<Int, QuestionItem>>
    ): PagedModel<EntityModel<Revision<Int, QuestionItem>>> {
        val entities: Page<Revision<Int, QuestionItem>> =
            auditRepository.findRevisionsByIdAndChangeKindNotIn(id, changekinds, pageable)
        return assembler.toModel(entities)
    }

    @ResponseBody
    @RequestMapping(value = ["/pdf/{id}/{revision}"], method = [RequestMethod.GET], produces = ["application/pdf"])
    fun getPdf(@PathVariable("id") id: UUID, @PathVariable("revision") revision: Int): ByteArray {
        auditRepository.findRevision(id, revision).also {
            return if(it.isPresent)
                it.get().entity.makePdf().toByteArray()
            else
                ByteArray(0)
        }
    }


}
