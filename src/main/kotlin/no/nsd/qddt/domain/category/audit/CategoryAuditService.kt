package no.nsd.qddt.domain.category.audit

import no.nsd.qddt.domain.AbstractEntityAudit.ChangeKind
import no.nsd.qddt.domain.category.Category
import no.nsd.qddt.domain.classes.interfaces.BaseServiceAudit
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import java.util.*

/**
 * @author Dag Østgulen Heradstveit
 */
interface CategoryAuditService : BaseServiceAudit<Category?, UUID?, Int?> {
    fun findRevisionByIdAndChangeKindNotIn(
        id: UUID,
        changeKinds: Collection<ChangeKind?>?,
        pageable: Pageable?
    ): Page<Revision<Int?, Category?>?> //    Revision<Integer,Category> findVersion(UUID id, String version);
}
