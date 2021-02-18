package no.nsd.qddt.repository

import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
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
interface QuestionItemRepositoryAudit : RevisionRepository<QuestionItem, UUID, Int> {
    fun findRevisionsByIdAndChangeKindNotIn(
        uuid: UUID,
        changeKinds: Collection<ChangeKind>,
        pageable: Pageable
    ): Page<Revision<Int, QuestionItem>>
}
