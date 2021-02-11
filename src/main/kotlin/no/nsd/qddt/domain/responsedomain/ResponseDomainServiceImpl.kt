package no.nsd.qddt.domain.responsedomain

import no.nsd.qddt.classes.exception.ResourceNotFoundException
import no.nsd.qddt.domain.category.CategoryRepository
import no.nsd.qddt.domain.responsedomain.audit.ResponseDomainAuditRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import no.nsd.qddt.utils.FilterTool.defaultOrModifiedSort
import no.nsd.qddt.utils.StringTool.likeify
import java.util.*

/**
* @author Stig Norland
* @author Dag Østgulen Heradstveit
*/
@Service("responseDomainService")
internal class ResponseDomainServiceImpl:ResponseDomainService {

  protected val LOG = LoggerFactory.getLogger(this.javaClass)

  @Autowired
  private lateinit var responseDomainRepository:ResponseDomainRepository
  @Autowired
  private lateinit var responseDomainAuditRepository:ResponseDomainAuditRepository
  @Autowired
  private lateinit var categoryRepository : CategoryRepository

  override fun count():Long {
    return responseDomainRepository.count()
  }
  override fun exists(id:UUID):Boolean {
    return responseDomainRepository.existsById(id)
  }

  override fun <S : ResponseDomain?> findOne(id: UUID): S {
    return responseDomainRepository.findById(id).map{
      postLoadProcessing(it)
    }.orElseThrow {
      ResourceNotFoundException(id,ResponseDomain::class.java)
    } as S
  }


  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
  override fun findBy(responseKind:ResponseKind, name:String, description:String, question:String, anchor:String, xmlLang:String, pageable:Pageable):Page<ResponseDomain> {
    val isEmpty = (name.isEmpty() && description.isEmpty() && question.isEmpty() && anchor.isEmpty())
    return responseDomainRepository.findByQuery(
      responseKind.toString(),
      likeify(if (isEmpty) "%" else  name),
      likeify(description),
      likeify(question),
      likeify(anchor),
      likeify(xmlLang),
      defaultOrModifiedSort(pageable, "name ASC"))
    .map{ this.postLoadProcessing(it) }
  }
  @Transactional(propagation = Propagation.NEVER)
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR') and hasPermission(#instance,'AGENCY')")
  override fun <S : ResponseDomain?> save(instance: S): S {
   LOG.info(Thread.currentThread().stackTrace.toString())
    return postLoadProcessing(
      responseDomainRepository.save(
        prePersistProcessing(instance as ResponseDomain))) as S
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR')")
  override fun delete(uuid:UUID) {
    responseDomainRepository.deleteById(uuid)
  }

  private fun prePersistProcessing(instance:ResponseDomain):ResponseDomain {
    val rdf = ResponseDomainFactory()
      when {
          instance.isBasedOn -> {
            val rev = responseDomainAuditRepository.findLastChangeRevision(instance.id).get().revisionNumber
            rdf.copy(instance, rev.orElse(null))
          }
          instance.isNewCopy -> {
            rdf.copy(instance, null)
          }
          else -> instance
      }.apply {
        if (this.codes.size == 0)
          this.populateCodes()
        this.beforeUpdate()             // TODO fix replicated framework func
        this.managedRepresentation = categoryRepository.save(this.managedRepresentation)
        return this
      }
  }
  private fun postLoadProcessing(instance:ResponseDomain):ResponseDomain {
    instance.changeComment =""
    return instance
  }
}
