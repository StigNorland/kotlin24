package no.nsd.qddt.service

import no.nsd.qddt.model.ControlConstruct
import no.nsd.qddt.model.QuestionConstruct
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.RepLoaderService
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
@Service("repLoaderService")
class RepLoaderServiceImpl : RepLoaderService {
    internal val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    @Autowired private var conceptRepository: ConceptRepository? = null
    @Autowired private var responseDomainRepository: ResponseDomainRepository? = null
    @Autowired private var categoryRepository: CategoryRepository? = null
    @Autowired private var questionItemRepository: QuestionItemRepository? = null
    @Autowired private var controlConstructRepository: ControlConstructRepository<ControlConstruct>? = null
    @Autowired private var questionConstructRepository: QuestionConstructRepository? = null // ControlConstructRepository<QuestionConstruct>? = null
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
        return when (elementKind){
            ElementKind.SURVEY_PROGRAM -> surveyProgramRepository
            ElementKind.STUDY -> studyRepository
            ElementKind.TOPIC_GROUP -> topicGroupRepository
            ElementKind.CONCEPT -> conceptRepository
            ElementKind.QUESTION_ITEM -> questionItemRepository
            ElementKind.RESPONSEDOMAIN -> responseDomainRepository
            ElementKind.CATEGORY -> categoryRepository
            ElementKind.INSTRUMENT -> instrumentRepository
            ElementKind.PUBLICATION -> publicationRepository
            ElementKind.CONTROL_CONSTRUCT -> controlConstructRepository
            ElementKind.QUESTION_CONSTRUCT -> questionConstructRepository
            ElementKind.STATEMENT_CONSTRUCT ,
            ElementKind.CONDITION_CONSTRUCT,
            ElementKind.SEQUENCE_CONSTRUCT -> controlConstructRepository
//            ElementKind.INSTRUCTION -> TODO()
//            ElementKind.UNIVERSE -> TODO()
//            ElementKind.COMMENT -> TODO()
            else -> {
                throw EntityNotFoundException("ElementKind :${elementKind} not defined.")
            }
        } as RevisionRepository<T,UUID,Int>
    }

}


// public interface RepLoader {

//     fun <T> getRepository(elementKind: ElementKind): RevisionRepository<T, UUID, Int>
// }
