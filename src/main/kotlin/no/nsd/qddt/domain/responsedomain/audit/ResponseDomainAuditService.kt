package no.nsd.qddt.domain.responsedomain.audit
import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.domain.classes.interfaces.BaseServiceAudit
import no.nsd.qddt.domain.responsedomain.ResponseDomain
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import java.util.UUID
/**
* @author Dag Østgulen Heradstveit
*/
internal interface ResponseDomainAuditService:BaseServiceAudit<ResponseDomain, UUID, Int> {
  fun findRevisionByIdAndChangeKindNotIn(id:UUID, changeKinds:Collection<AbstractEntityAudit.ChangeKind>, pageable:Pageable):Page<Revision<Int, ResponseDomain>>
}