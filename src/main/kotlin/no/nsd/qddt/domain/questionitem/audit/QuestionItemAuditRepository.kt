package no.nsd.qddt.domain.questionitem.audit

import no.nsd.qddt.classes.AbstractEntityAudit
import no.nsd.qddt.domain.questionitem.QuestionItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author Dag Østgulen Heradstveit
 */
@Repository
internal interface QuestionItemAuditRepository : RevisionRepository<QuestionItem, UUID, Int> {
    fun findRevisionsByIdAndChangeKindNotIn(
        uuid: UUID,
        changeKinds: Collection<AbstractEntityAudit.ChangeKind>,
        pageable: Pageable
    ): Page<Revision<Int, QuestionItem>>
}
