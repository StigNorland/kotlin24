package no.nsd.qddt.domain.questionitem
import no.nsd.qddt.classes.elementref.ElementLoader
import no.nsd.qddt.classes.elementref.ParentRef
import no.nsd.qddt.classes.exception.ResourceNotFoundException
import no.nsd.qddt.classes.exception.StackTraceFilter
import no.nsd.qddt.domain.concept.Concept
import no.nsd.qddt.domain.concept.ConceptService
import no.nsd.qddt.domain.questionitem.audit.QuestionItemAuditService
import no.nsd.qddt.domain.responsedomain.ResponseDomain
import no.nsd.qddt.domain.responsedomain.ResponseDomainService
import no.nsd.qddt.domain.responsedomain.ResponseKind
import no.nsd.qddt.domain.responsedomain.audit.ResponseDomainAuditService
import no.nsd.qddt.domain.topicgroup.TopicGroup
import no.nsd.qddt.domain.topicgroup.TopicGroupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import java.util.stream.Collectors
import no.nsd.qddt.utils.FilterTool.defaultOrModifiedSort
import no.nsd.qddt.utils.FilterTool.defaultSort
import no.nsd.qddt.utils.StringTool.IsNullOrTrimEmpty
import no.nsd.qddt.utils.StringTool.likeify
/**
* @author Stig Norland
*/
@Service("questionItemService")
internal class QuestionItemServiceImpl @Autowired
constructor(questionItemRepository:QuestionItemRepository,
            responseDomainAuditService:ResponseDomainAuditService,
            conceptService:ConceptService,
            topicGroupService:TopicGroupService,
            questionItemAuditService:QuestionItemAuditService,
            responseDomainService:ResponseDomainService):QuestionItemService {
  protected val LOG = LoggerFactory.getLogger(this.javaClass)
  private val questionItemRepository:QuestionItemRepository
  private val auditService:QuestionItemAuditService
  private val conceptService:ConceptService
  private val responseDomainService:ResponseDomainService
  private val rdLoader:ElementLoader<ResponseDomain>
  private val topicGroupService:TopicGroupService
  init{
    this.questionItemRepository = questionItemRepository
    this.auditService = questionItemAuditService
    this.conceptService = conceptService
    this.topicGroupService = topicGroupService
    this.responseDomainService = responseDomainService
    this.rdLoader = ElementLoader(responseDomainAuditService)
  }
  fun count():Long {
    return questionItemRepository.count()
  }
  fun exists(uuid:UUID):Boolean {
    return questionItemRepository.existsById(uuid)
  }
  fun findOne(uuid:UUID):QuestionItem {
    return postLoadProcessing(questionItemRepository.findById(uuid).orElseThrow(
      { ResourceNotFoundException(uuid, QuestionItem::class.java) })
                             )
  }
  @Transactional
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR') and hasPermission(#instance,'AGENCY')")
  fun save(instance:QuestionItem):QuestionItem {
    try
    {
      return postLoadProcessing(
        questionItemRepository.save(
          prePersistProcessing(instance)))
    }
    catch (ex:Exception) {
      LOG.error("QI save ->", ex)
      throw ex
    }
  }
  //
  // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR')")
  // public List<QuestionItem> save(List<QuestionItem> instances) {
  // return instances.stream().map(this::save).collect(Collectors.toList());
  // }
  fun delete(uuid:UUID) {
    delete(questionItemRepository.getOne(uuid))
  }
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR') and hasPermission(#instance,'AGENCY')")
  fun delete(instance:QuestionItem) {
    try
    {
      // TODO fix auto delete when mixed responsedomiain are reused.
      if (instance.getResponseDomainRef().getElementId() != null)
      {
        val rd = responseDomainService.findOne(instance.getResponseDomainRef().getElementId())
        if (rd.getResponseKind() === ResponseKind.MIXED)
        {
          responseDomainService.delete(instance.getResponseDomainRef().getElementId())
        }
      }
      questionItemRepository.delete(instance)
    }
    catch (ex:Exception) {
      LOG.error("QI delete ->", ex)
      throw ex
    }
  }
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
  fun findAllPageable(pageable:Pageable):Page<QuestionItem> {
    try
    {
      return questionItemRepository.findAll(
        defaultSort(pageable, "name"))
      // .map(this::postLoadProcessing);
    }
    catch (ex:Exception) {
      LOG.error("QI catch & continue ->", ex)
      return PageImpl(null)
    }
  }
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
  fun findByNameOrQuestionOrResponseName(name:String, question:String, responseName:String, xmlLang:String, pageable:Pageable):Page<QuestionItem> {
    pageable = defaultOrModifiedSort(pageable, "name ASC", "updated DESC")
    if (IsNullOrTrimEmpty(name) && IsNullOrTrimEmpty(responseName) && IsNullOrTrimEmpty(question))
    {
      name = "%"
    }
    return questionItemRepository.findByNames(likeify(name), likeify(question), likeify(responseName), likeify(xmlLang), pageable)
  }
  /*
 post fetch processing, some elements are not supported by the framework (enver mixed with jpa db queries)
 thus we need to populate some elements ourselves.
 */
  private fun postLoadProcessing(instance:QuestionItem):QuestionItem {
    // LOG.info( "POST LOAD" );
    try
    {
      if (instance.getResponseDomainRef().getElementId() != null && instance.getResponseDomainRef().element == null)
      {
        rdLoader.fill(instance.getResponseDomainRef())
      }
      else
      {
        LOG.info("no RD in this QI")
      }
      val list = conceptService.findByQuestionItem(instance.getId(), null).stream()
      .map(???({ ParentRef<Concept>() }))
      .collect(Collectors.toList<T>())
      list.addAll(topicGroupService.findByQuestionItem(instance.getId(), null).stream()
                  .map(???({ ParentRef<TopicGroup>() }))
                  .collect(Collectors.toList<T>()))
      instance.setParentRefs(list)
    }
    catch (ex:Exception) {
      StackTraceFilter.println(ex.getStackTrace())
      println(ex.message)
    }
    return instance
  }
  private fun prePersistProcessing(instance:QuestionItem):QuestionItem {
    // LOG.info( "PRE PERSIST" );
    val rev:Int = null
    if (instance.isBasedOn())
    {
      LOG.info("PRE PERSIST -> BASED ON")
      rev = if ((instance.getBasedOnRevision() != null)) instance.getBasedOnRevision() else auditService.findLastChange(instance.getId()).getRevisionNumber().get()
    }
    if (instance.isBasedOn() || instance.isNewCopy())
    {
      LOG.info("PRE PERSIST -> MAKE A COPY")
      instance = QuestionItemFactory().copy(instance, rev)
    }
    return instance
  }
}