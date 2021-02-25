package no.nsd.qddt.service

import no.nsd.qddt.model.ControlConstruct
import no.nsd.qddt.model.QuestionConstruct
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
@Service
class RepositoryLoaderImpl : RepositoryLoader {
    internal val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    @Autowired private var conceptRepository: ConceptRepository? = null
    @Autowired private var responseDomainRepository: ResponseDomainRepository? = null

    @Autowired private var questionItemRepository: QuestionItemRepository? = null
    @Autowired private var controlConstructRepository: ControlConstructRepository<ControlConstruct>? = null
    @Autowired private var questionConstructRepository: ControlConstructRepository<QuestionConstruct>? = null
//    @Autowired private var controlConstructRepository: ControlConstructRepository<ControlConstruct>? = null
//    @Autowired private var controlConstructRepository: ControlConstructRepository<ControlConstruct>? = null
//    @Autowired private var controlConstructRepository: ControlConstructRepository<ControlConstruct>? = null
    @Autowired private var topicGroupRepository: TopicGroupRepository? = null
    @Autowired private var surveyProgramRepository: SurveyProgramRepository? = null
    @Autowired private var studyRepository: StudyRepository? = null
    @Autowired private var publicationRepository: PublicationRepository? = null
    @Autowired private var instrumentRepository: InstrumentRepository? = null

    override fun <T> getRepository(elementKind: ElementKind): RevisionRepository<T, UUID, Int> {
        logger.info("get Service -> $elementKind")
        return when (elementKind) {
            ElementKind.CONCEPT -> conceptRepository as RevisionRepository<T,UUID,Int>
            ElementKind.QUESTION_CONSTRUCT -> questionConstructRepository as RevisionRepository<T,UUID,Int>
                ElementKind.CONTROL_CONSTRUCT,
                ElementKind.STATEMENT_CONSTRUCT,
                ElementKind.SEQUENCE_CONSTRUCT,
                ElementKind.CONDITION_CONSTRUCT -> controlConstructRepository as RevisionRepository<T,UUID,Int>
            ElementKind.RESPONSEDOMAIN -> responseDomainRepository as RevisionRepository<T,UUID,Int>
            ElementKind.INSTRUMENT -> instrumentRepository as RevisionRepository<T,UUID,Int>
            ElementKind.QUESTION_ITEM -> questionItemRepository as RevisionRepository<T,UUID,Int>
            ElementKind.STUDY -> studyRepository as RevisionRepository<T,UUID,Int>
            ElementKind.SURVEY_PROGRAM -> surveyProgramRepository as RevisionRepository<T,UUID,Int>
            ElementKind.TOPIC_GROUP -> topicGroupRepository as RevisionRepository<T,UUID,Int>
            ElementKind.PUBLICATION -> publicationRepository as RevisionRepository<T,UUID,Int>
            else -> {
                throw EntityNotFoundException("ElementKind :${elementKind.className} not defined.")
            }
        }
    }

}

