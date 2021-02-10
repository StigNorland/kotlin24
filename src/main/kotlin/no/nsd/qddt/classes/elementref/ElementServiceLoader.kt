package no.nsd.qddt.classes.elementref

//import no.nsd.qddt.domain.concept.audit.ConceptAuditService
import no.nsd.qddt.classes.interfaces.BaseServiceAudit
import no.nsd.qddt.domain.questionitem.audit.QuestionItemAuditService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author Stig Norland
 */
@Service("elementServiceLoader123")
class ElementServiceLoader @Autowired constructor(
//    conceptService: ConceptAuditService,
//    controlConstructService: ControlConstructAuditService,
//    instrumentService: InstrumentAuditService,
//    publicationService: PublicationAuditService,
//    questionItemService: QuestionItemAuditService,
//    responseDomainService: ResponseDomainAuditService,
//    studyService: StudyAuditService,
//    surveyProgramService: SurveyProgramAuditService,
//    topicGroupService: TopicGroupAuditService
) {
    protected val LOG = LoggerFactory.getLogger(this.javaClass)
//    private val conceptService: ConceptAuditService
//    private val controlConstructService: ControlConstructAuditService
//    private val instrumentService: InstrumentAuditService
//    private val publicationService: PublicationAuditService
//    private val questionItemService: QuestionItemAuditService
//    private val responseDomainService: ResponseDomainAuditService
//    private val studyService: StudyAuditService
//    private val surveyProgramService: SurveyProgramAuditService
//    private val topicGroupService: TopicGroupAuditService

    fun getService(elementKind: ElementKind): BaseServiceAudit<*, *, *>? {
        LOG.info("get Service -> $elementKind")
        return when (elementKind) {
//            ElementKind.CONCEPT -> conceptService
//            ElementKind.CONTROL_CONSTRUCT, ElementKind.QUESTION_CONSTRUCT, ElementKind.STATEMENT_CONSTRUCT, ElementKind.SEQUENCE_CONSTRUCT, ElementKind.CONDITION_CONSTRUCT -> controlConstructService
//            ElementKind.RESPONSEDOMAIN -> responseDomainService
//            ElementKind.INSTRUMENT -> instrumentService
//            ElementKind.QUESTION_ITEM -> questionItemService
//            ElementKind.STUDY -> studyService
//            ElementKind.SURVEY_PROGRAM -> surveyProgramService
//            ElementKind.TOPIC_GROUP -> topicGroupService
//            ElementKind.PUBLICATION -> publicationService
            else -> {
                LOG.error("ElementKind :" + elementKind.className + " not defined.")
                null
            }
        }
    }

    init {
        LOG.info("ElementServiceLoader -> ")
//        this.controlConstructService = controlConstructService
//        this.instrumentService = instrumentService
//        this.publicationService = publicationService
//        this.questionItemService = questionItemService
//        this.responseDomainService = responseDomainService
//        this.studyService = studyService
//        this.surveyProgramService = surveyProgramService
//        this.topicGroupService = topicGroupService
//        this.conceptService = conceptService
    }
}
