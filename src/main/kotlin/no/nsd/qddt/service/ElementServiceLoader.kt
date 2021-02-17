package no.nsd.qddt.service

import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.BaseServiceAudit
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author Stig Norland
 */
@Service("elementServiceLoader")
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
    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)
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
        logger.info("get Service -> $elementKind")
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
                logger.error("ElementKind :" + elementKind.className + " not defined.")
                null
            }
        }
    }

    init {
        logger.info("ElementServiceLoader -> ")
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
