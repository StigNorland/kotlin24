package no.nsd.qddt.domain.responsedomain
import no.nsd.qddt.domain.category.CategoryService
import no.nsd.qddt.classes.exception.ResourceNotFoundException
import no.nsd.qddt.domain.responsedomain.audit.ResponseDomainAuditService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import no.nsd.qddt.utils.FilterTool.defaultOrModifiedSort
import no.nsd.qddt.utils.StringTool.likeify
/**
* @author Dag Østgulen Heradstveit
* @author Stig Norland
*/
@Service("responseDomainService")
internal class ResponseDomainServiceImpl @Autowired
constructor(responseDomainRepository:ResponseDomainRepository,
            categoryService:CategoryService,
            responseDomainAuditService:ResponseDomainAuditService):ResponseDomainService {
  protected val LOG = LoggerFactory.getLogger(this.javaClass)
  private val responseDomainRepository:ResponseDomainRepository
  private val auditService:ResponseDomainAuditService
  private val categoryService:CategoryService
  init{
    this.responseDomainRepository = responseDomainRepository
    this.categoryService = categoryService
    this.auditService = responseDomainAuditService
  }
  fun count():Long {
    return responseDomainRepository.count()
  }
  fun exists(uuid:UUID):Boolean {
    return responseDomainRepository.existsById(uuid)
  }
  fun findOne(uuid:UUID):ResponseDomain {
    return responseDomainRepository.findById(uuid).map(???({ this.postLoadProcessing(it) })).orElseThrow(
      { ResourceNotFoundException(uuid, ResponseDomain::class.java) })
  }
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
  fun findBy(responseKind:ResponseKind, name:String, description:String, question:String, anchor:String, xmlLang:String, pageable:Pageable):Page<ResponseDomain> {
    if (name.isEmpty() && description.isEmpty() && question.isEmpty() && anchor.isEmpty())
    {
      name = "%"
    }
    return responseDomainRepository.findByQuery(
      responseKind.toString(),
      likeify(name),
      likeify(description),
      likeify(question),
      likeify(anchor),
      likeify(xmlLang),
      defaultOrModifiedSort(pageable, "name ASC"))
    .map(???({ this.postLoadProcessing(it) }))
  }
  @Transactional(propagation = Propagation.NEVER)
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR') and hasPermission(#instance,'AGENCY')")
  fun save(instance:ResponseDomain):ResponseDomain {
    LOG.info(Thread.currentThread().getStackTrace().toString())
    return postLoadProcessing(
      responseDomainRepository.save(
        prePersistProcessing(instance)))
  }
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR')")
  fun delete(uuid:UUID) {
    responseDomainRepository.deleteById(uuid)
  }
  private fun prePersistProcessing(instance:ResponseDomain):ResponseDomain {
    val rdf = ResponseDomainFactory()
    if (instance.isBasedOn())
    {
      val rev = auditService.findLastChange(instance.getId()).getRevisionNumber().get()
      instance = rdf.copy(instance, rev)
    }
    else if (instance.isNewCopy())
    {
      instance = rdf.copy(instance, null)
    }
    // read the codes from the MR, into the RD
    if (instance.getCodes().size() === 0)
    instance.populateCodes()
    // TODO fix replicated framework func
    instance.beforeUpdate()
    instance.setManagedRepresentation(
      categoryService.save(
        instance.getManagedRepresentation()))
    return instance
  }
  private fun postLoadProcessing(instance:ResponseDomain):ResponseDomain {
    instance.setChangeComment(null)
    return instance
  }
}