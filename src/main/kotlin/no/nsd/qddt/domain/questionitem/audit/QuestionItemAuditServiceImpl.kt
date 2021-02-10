//package no.nsd.qddt.domain.questionitem.audit
//
//import no.nsd.qddt.classes.AbstractAuditFilter
//import no.nsd.qddt.classes.AbstractEntityAudit
//import no.nsd.qddt.classes.elementref.ElementLoader
//import no.nsd.qddt.domain.AbstractAuditFilter
//import no.nsd.qddt.domain.questionitem.QuestionItem
//import no.nsd.qddt.domain.responsedomain.ResponseDomain
//import org.hibernate.Hibernate
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.Pageable
//import org.springframework.data.history.Revision
//import org.springframework.stereotype.Service
//import java.util.*
//
///**
// * @author Dag Østgulen Heradstveit
// */
//@Service("questionItemAuditService")
//internal class QuestionItemAuditServiceImpl @Autowired constructor(
//    private val questionItemAuditRepository: QuestionItemAuditRepository,
//    responseDomainAuditService: ResponseDomainAuditService?
//) : AbstractAuditFilter<Int?, QuestionItem?>(), QuestionItemAuditService {
//    protected val LOG = LoggerFactory.getLogger(this.javaClass)
//    private val rdLoader: ElementLoader<ResponseDomain>
//    private var showPrivateComments = false
//    fun findLastChange(uuid: UUID?): Revision<Int, QuestionItem> {
//        return postLoadProcessing(questionItemAuditRepository.findLastChangeRevision(uuid).get())
//    }
//
//    fun findRevision(uuid: UUID?, revision: Int?): Revision<Int, QuestionItem> {
//        return postLoadProcessing(questionItemAuditRepository.findRevision(uuid, revision).get())
//    }
//
//    fun findRevisions(uuid: UUID?, pageable: Pageable?): Page<Revision<Int, QuestionItem>> {
//        return questionItemAuditRepository.findRevisions(uuid, pageable)
//            .map { instance: Revision<Int?, QuestionItem?> -> postLoadProcessing(instance) }
//    }
//
//    fun findFirstChange(uuid: UUID?): Revision<Int, QuestionItem> {
//        return postLoadProcessing(
//            questionItemAuditRepository.findRevisions(uuid)
//                .reverse().getContent().get(0)
//        )
//    }
//
//    fun setShowPrivateComment(showPrivate: Boolean) {
//        showPrivateComments = showPrivate
//    }
//
//    fun findRevisionByIdAndChangeKindNotIn(
//        id: UUID?,
//        changeKinds: Collection<AbstractEntityAudit.ChangeKind?>?,
//        pageable: Pageable?
//    ): Page<Revision<Int, QuestionItem>> {
//        return getPage(questionItemAuditRepository.findRevisions(id), changeKinds, pageable)
//    }
//
//    fun getQuestionItemLastOrRevision(id: UUID, revision: Int?): Revision<Int, QuestionItem>? {
//        val retval: Revision<Int, QuestionItem> =
//            if (revision == null || revision <= 0) findLastChange(id) else findRevision(id, revision)
//        if (retval == null) LOG.info("getQuestionItemLastOrRevision returned with null ($id,$revision)")
//        return retval
//    }
//
//    protected fun postLoadProcessing(instance: Revision<Int?, QuestionItem?>): Revision<Int?, QuestionItem?> {
//        if (instance.getEntity().responseDomainRef != null && instance.getEntity().responseDomainRef.elementId != null) {
//            rdLoader.fill(instance.getEntity().responseDomainRef)
//        }
//
//
////        Hibernate.initialize( instance.getEntity().get) );
////        List<LeafRef<?>> list = conceptService.findByQuestionItem(instance.getId(),null).stream()
////            .map( ConceptRef::new )
////            .collect( Collectors.toList());
////
////        list.addAll(topicGroupService.findByQuestionItem(instance.getId(),null).stream()
////            .map( TopicRef::new )
////            .collect( Collectors.toList()));
////
////        instance.setParentRefs( list);
//        Hibernate.initialize(instance.getEntity().comments)
//        instance.getEntity().version.revision = instance.getRevisionNumber().get()
//        return instance
//    }
//
//    init {
//        rdLoader = ElementLoader<ResponseDomain>(responseDomainAuditService)
//    }
//}
