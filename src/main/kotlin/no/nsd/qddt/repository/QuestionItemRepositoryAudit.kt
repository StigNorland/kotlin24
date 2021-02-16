package no.nsd.qddt.repository

import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import no.nsd.qddt.repository.projection.QuestionItemListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Dag Østgulen Heradstveit
 */
@RepositoryRestResource(path = "questionitems", collectionResourceRel = "questionItem", itemResourceRel = "QuestionItem", excerptProjection = QuestionItemListe::class)
interface QuestionItemRepositoryAudit : RevisionRepository<QuestionItem, UUID, Int> {
    fun findRevisionsByIdAndChangeKindNotIn(
        uuid: UUID,
        changeKinds: Collection<ChangeKind>,
        pageable: Pageable
    ): Page<Revision<Int, QuestionItem>>
}
