package no.nsd.qddt.domain.controlconstruct.audit

import no.nsd.qddt.domain.AbstractEntityAudit.ChangeKind
import no.nsd.qddt.domain.classes.interfaces.BaseServiceAudit
import no.nsd.qddt.domain.controlconstruct.pojo.ControlConstruct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import java.util.*

/**
 * @author Dag Østgulen Heradstveit
 */
interface ControlConstructAuditService : BaseServiceAudit<ControlConstruct?, UUID?, Int?> {
    fun findRevisionsByChangeKindNotIn(
        id: UUID,
        changeKinds: Collection<ChangeKind?>?,
        pageable: Pageable?
    ): Page<Revision<Int?, ControlConstruct?>?>
}
