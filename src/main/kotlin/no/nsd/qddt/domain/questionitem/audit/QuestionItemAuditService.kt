package no.nsd.qddt.domain.questionitem.audit

import no.nsd.qddt.domain.questionitem.QuestionItem
import java.util.UUID
import no.nsd.qddt.classes.AbstractEntityAudit
import no.nsd.qddt.classes.interfaces.BaseServiceAudit
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision

/**
 * @author Dag Østgulen Heradstveit
 */
internal interface QuestionItemAuditService : BaseServiceAudit<QuestionItem, UUID, Int> {
    fun findRevisionByIdAndChangeKindNotIn(
        id: UUID,
        changeKinds: Collection<AbstractEntityAudit.ChangeKind>,
        pageable: Pageable
    ): Page<Revision<Int, QuestionItem>>

    fun getQuestionItemLastOrRevision(id: UUID, revision: Int): Revision<Int, QuestionItem>
}
