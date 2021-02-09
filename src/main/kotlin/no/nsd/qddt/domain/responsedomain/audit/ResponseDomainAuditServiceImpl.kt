// package no.nsd.qddt.domain.responsedomain.audit
// import no.nsd.qddt.domain.AbstractAuditFilter
// import no.nsd.qddt.domain.AbstractEntityAudit
// import no.nsd.qddt.domain.responsedomain.ResponseDomain
// import org.hibernate.Hibernate
// import org.slf4j.Logger
// import org.slf4j.LoggerFactory
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.data.domain.Page
// import org.springframework.data.domain.Pageable
// import org.springframework.data.history.Revision
// import org.springframework.stereotype.Service
// import java.util.UUID
// /**
// * @author Dag Østgulen Heradstveit
// */
// @Service("responseDomainAuditService")
// internal class ResponseDomainAuditServiceImpl @Autowired
// constructor(responseDomainAuditRepository:ResponseDomainAuditRepository):AbstractAuditFilter<Int, ResponseDomain>(), ResponseDomainAuditService {
//   protected val LOG = LoggerFactory.getLogger(this.getClass())
//   private val responseDomainAuditRepository:ResponseDomainAuditRepository
//   private val showPrivateComments:Boolean = false
//   init{
//     this.responseDomainAuditRepository = responseDomainAuditRepository
//   }
//   fun findLastChange(uuid:UUID):Revision<Int, ResponseDomain> {
//     return postLoadProcessing(responseDomainAuditRepository.findLastChangeRevision(uuid).get())
//   }
//   fun findRevision(uuid:UUID, revision:Int):Revision<Int, ResponseDomain> {
//     return postLoadProcessing(responseDomainAuditRepository.findRevision(uuid, revision).get())
//   }
//   fun findRevisions(uuid:UUID, pageable:Pageable):Page<Revision<Int, ResponseDomain>> {
//     return responseDomainAuditRepository.findRevisions(uuid, pageable)
//     .map(???({ this.postLoadProcessing(it) }))
//   }
//   fun findFirstChange(uuid:UUID):Revision<Int, ResponseDomain> {
//     return postLoadProcessing(
//       responseDomainAuditRepository.findRevisions(uuid)
//       .reverse().getContent().get(0))
//   }
//   fun setShowPrivateComment(showPrivate:Boolean) {
//     showPrivateComments = showPrivate
//   }
//   fun findRevisionByIdAndChangeKindNotIn(id:UUID, changeKinds:Collection<AbstractEntityAudit.ChangeKind>, pageable:Pageable):Page<Revision<Int, ResponseDomain>> {
//     return getPage(responseDomainAuditRepository.findRevisions(id), changeKinds, pageable)
//   }
//   protected fun postLoadProcessing(instance:Revision<Int, ResponseDomain>):Revision<Int, ResponseDomain> {
//     assert((instance != null))
//     try
//     {
//       Hibernate.initialize(instance.getEntity().comments)
//       Hibernate.initialize(instance.getEntity().getManagedRepresentation()) //Lazy loading trick... (we want the MRep when locking at a revision).
//     }
//     catch (ex:Exception) {
//       LOG.error("postLoadProcessing", ex)
//     }
//     return instance
//   }
// }
