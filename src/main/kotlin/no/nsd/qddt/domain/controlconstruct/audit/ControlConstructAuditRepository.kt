package no.nsd.qddt.domain.controlconstruct.audit

import no.nsd.qddt.domain.AbstractEntityAudit.ChangeKind
import no.nsd.qddt.domain.controlconstruct.pojo.ControlConstruct
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
internal interface ControlConstructAuditRepository : RevisionRepository<ControlConstruct?, UUID?, Int?> {
    fun findRevisionsByIdAndChangeKindNotIn(
        uuid: UUID?,
        changeKinds: Collection<ChangeKind?>?,
        pageable: Pageable?
    ): Page<Revision<Int?, ControlConstruct?>?>?
}
