package no.nsd.qddt.service

import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.repository.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityNotFoundException

/**
 * @author Stig Norland
 */
@Service("elementRepositoryLoader")
class ElementRepositoryLoader {
    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var conceptRepository: ConceptRepository
    @Autowired
    private lateinit var controlConstructRepository: ControlConstructRepository
    @Autowired
    private lateinit var instrumentRepository: InstrumentRepository
    @Autowired
    private lateinit var publicationRepository: PublicationRepository
    @Autowired
    private lateinit var questionItemRepository: QuestionItemRepository
    @Autowired
    private lateinit var responseDomainRepository: ResponseDomainRepository
    @Autowired
    private lateinit var studyRepository: StudyRepository
    @Autowired
    private lateinit var surveyProgramRepository: SurveyProgramRepository
    @Autowired
    private lateinit var topicGroupRepository: TopicGroupRepository


    fun getRepository(elementKind: ElementKind): RevisionRepository<*, UUID,Int> {
        logger.info("get Service -> $elementKind")
        return when (elementKind) {
            ElementKind.CONCEPT -> conceptRepository
                ElementKind.CONTROL_CONSTRUCT,
                ElementKind.QUESTION_CONSTRUCT,
                ElementKind.STATEMENT_CONSTRUCT,
                ElementKind.SEQUENCE_CONSTRUCT,
                ElementKind.CONDITION_CONSTRUCT -> controlConstructRepository
            ElementKind.RESPONSEDOMAIN -> responseDomainRepository
            ElementKind.INSTRUMENT -> instrumentRepository
            ElementKind.QUESTION_ITEM -> questionItemRepository
            ElementKind.STUDY -> studyRepository
            ElementKind.SURVEY_PROGRAM -> surveyProgramRepository
            ElementKind.TOPIC_GROUP -> topicGroupRepository
            ElementKind.PUBLICATION -> publicationRepository
            else -> {
                throw EntityNotFoundException("ElementKind :${elementKind.className} not defined.")
            }
        }
    }

}
