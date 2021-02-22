package no.nsd.qddt.repository

import no.nsd.qddt.model.*
import no.nsd.qddt.model.enums.ElementKind
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.stereotype.Component
import java.util.*
import javax.persistence.EntityNotFoundException

/**
 * @author Stig Norland
 */
@Component
class ElementRepositoryLoader {
    @Autowired
    constructor(conceptRepository: ConceptRepository, responseDomainRepository: ResponseDomainRepository) {
        this.conceptRepository = conceptRepository
        this.responseDomainRepository = responseDomainRepository
    }

    internal val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val conceptRepository: ConceptRepository
    private val responseDomainRepository: ResponseDomainRepository
    @Autowired private val questionItemRepository: QuestionItemRepository? = null
    @Autowired private val controlConstructRepository: ControlConstructRepository? = null
    @Autowired private val topicGroupRepository: TopicGroupRepository? = null
    @Autowired private val surveyProgramRepository: SurveyProgramRepository? = null
    @Autowired private val studyRepository: StudyRepository? = null
    @Autowired private val publicationRepository: PublicationRepository? = null
    @Autowired private val instrumentRepository: InstrumentRepository? = null

    fun getRepository(elementKind: ElementKind): RevisionRepository<*, UUID,Int> {
        logger.info("get Service -> $elementKind")
        return when (elementKind) {
            ElementKind.CONCEPT -> conceptRepository
                ElementKind.CONTROL_CONSTRUCT,
                ElementKind.QUESTION_CONSTRUCT,
                ElementKind.STATEMENT_CONSTRUCT,
                ElementKind.SEQUENCE_CONSTRUCT,
                ElementKind.CONDITION_CONSTRUCT -> controlConstructRepository as RevisionRepository<ControlConstruct,UUID,Int>
            ElementKind.RESPONSEDOMAIN -> responseDomainRepository
            ElementKind.INSTRUMENT -> instrumentRepository as RevisionRepository<Instrument,UUID,Int>
            ElementKind.QUESTION_ITEM -> questionItemRepository as RevisionRepository<QuestionItem,UUID,Int>
            ElementKind.STUDY -> studyRepository as RevisionRepository<Study,UUID,Int>
            ElementKind.SURVEY_PROGRAM -> surveyProgramRepository as RevisionRepository<SurveyProgram,UUID,Int>
            ElementKind.TOPIC_GROUP -> topicGroupRepository as RevisionRepository<TopicGroup,UUID,Int>
            ElementKind.PUBLICATION -> publicationRepository as RevisionRepository<Publication,UUID,Int>
            else -> {
                throw EntityNotFoundException("ElementKind :${elementKind.className} not defined.")
            }
        }
    }

}
