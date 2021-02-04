package no.nsd.qddt.domain.controlconstruct

import no.nsd.qddt.domain.classes.exception.ResourceNotFoundException
import no.nsd.qddt.domain.controlconstruct.json.Converter
import no.nsd.qddt.domain.controlconstruct.pojo.Sequence
import no.nsd.qddt.domain.universe.Universe
import no.nsd.qddt.utils.FilterTool.defaultOrModifiedSort
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

/**
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
@Service("controlConstructService")
internal class ControlConstructServiceImpl @Autowired constructor(
    private val controlConstructRepository: ControlConstructRepository,
    controlConstructAuditService: ControlConstructAuditService,
    iService: InstructionService,
    uService: UniverseService,
    questionAuditService: QuestionItemAuditService?
) : ControlConstructService {
    protected val LOG = LoggerFactory.getLogger(this.javaClass)
    private val auditService: ControlConstructAuditService
    private val iService: InstructionService
    private val uService: UniverseService
    private val qiLoader: ElementLoader<QuestionItem>
    override fun count(): Long {
        return controlConstructRepository.count()
    }

    override fun exists(uuid: UUID): Boolean {
        return controlConstructRepository.existsById(uuid)
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
    override fun findOne(id: UUID): ControlConstruct {
        return controlConstructRepository.findById(id)
            .map(Function<ControlConstruct, ControlConstruct> { instance: ControlConstruct? ->
                postLoadProcessing(
                    instance
                )
            })
            .orElseThrow(Supplier<ResourceNotFoundException> {
                ResourceNotFoundException(
                    id,
                    ControlConstruct::class.java
                )
            })
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR') and hasPermission(#instance,'AGENCY')")
    override fun <S : ControlConstruct?> save(instance: S): S {
        return postLoadProcessing(
            controlConstructRepository.save(
                prePersistProcessing(instance)
            )
        )
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR') and hasPermission(#instance,'AGENCY')")
    fun save(instances: List<ControlConstruct>): List<ControlConstruct?> {
        return instances.stream()
            .map(Function<ControlConstruct, ControlConstruct> { instance: ControlConstruct -> this.save(instance) })
            .collect(Collectors.toList())
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR')")
    override fun delete(uuid: UUID) {
        controlConstructRepository.deleteById(uuid)
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
    override fun findByQuestionItems(questionItemIds: List<UUID?>): List<ConstructQuestionJson?>? {
        assert(questionItemIds.size > 0)
        return controlConstructRepository.findByQuestionItemRefElementId(questionItemIds[0]).stream()
            .map(Function<QuestionConstruct?, ConstructQuestionJson?> { c: QuestionConstruct? ->
                Converter.mapConstruct<ConstructJson>(
                    postLoadProcessing<QuestionConstruct?>(c)
                ) as ConstructQuestionJson
            })
            .collect(Collectors.toList())
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
    override fun <S : ConstructJsonView?> findBySearcAndControlConstructKind(
        kind: String?,
        superKind: String,
        name: String,
        description: String,
        xmlLang: String?,
        pageable: Pageable?
    ): Page<S> {
        var name = name
        var pageable = pageable
        pageable = defaultOrModifiedSort(pageable, "name ASC", "updated DESC")
        if (name.isEmpty() && description.isEmpty() && superKind.isEmpty()) {
            name = "%"
        }
        return controlConstructRepository.findByQuery<S?>(
            kind,
            superKind,
            likeify(name),
            likeify(description),
            "",
            "",
            xmlLang,
            pageable
        )
            .map { obj: S? -> Converter.mapConstructView() }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
    override fun <S : ConstructJsonView?> findQCBySearch(
        name: String,
        questionName: String,
        questionText: String,
        xmlLang: String?,
        pageable: Pageable?
    ): Page<S> {
        var name = name
        var pageable = pageable
        pageable = defaultOrModifiedSort(pageable, "name ASC", "updated DESC")
        if (name.isEmpty() && questionName.isEmpty() && questionText.isEmpty()) {
            name = "%"
        }
        return controlConstructRepository.findByQuery<ControlConstruct?>(
            "QUESTION_CONSTRUCT",
            "",
            name,
            "",
            questionName,
            questionText,
            xmlLang,
            pageable
        )
            .map { qi: ControlConstruct? -> Converter.mapConstructView(postLoadProcessing<ControlConstruct?>(qi)) }
    }

    private fun <S : ControlConstruct?> prePersistProcessing(instance: S): S? {
        var instance: S? = instance!!
        if (instance is QuestionConstruct) {
            val qc: QuestionConstruct? = instance as QuestionConstruct?
            qc.getControlConstructInstructions().stream()
                .filter(Predicate<ControlConstructInstruction> { cqi: ControlConstructInstruction -> cqi.getInstruction().id == null })
                .forEach(Consumer<ControlConstructInstruction> { cqi: ControlConstructInstruction ->
                    cqi.setInstruction(
                        iService.save(cqi.getInstruction())
                    )
                })
            qc.getUniverse().stream()
                .filter(Predicate { universe: Universe -> universe.id == null })
                .forEach(Consumer<Universe> { instance: Universe? -> uService.save(instance) })
            instance = qc
        }
        if (instance.isBasedOn || instance.isNewCopy) {
            val rev: Optional<Int> = auditService.findLastChange(instance.basedOnObject).getRevisionNumber()
            when (instance.classKind) {
                "QUESTION_CONSTRUCT" -> instance =
                    FactoryQuestionConstruct().copy(instance as QuestionConstruct?, rev.get())
                "SEQUENCE_CONSTRUCT" -> instance = FactorySequenceConstruct().copy(instance as Sequence?, rev.get())
                "CONDITION_CONSTRUCT" -> instance =
                    FactoryConditionConstruct().copy(instance as ConditionConstruct?, rev.get())
                "STATEMENT_CONSTRUCT" -> instance =
                    FactoryStatementConstruct().copy(instance as StatementItem?, rev.get())
            }
        }
        return instance
    }

    private fun <S : ControlConstruct?> postLoadProcessing(instance: S?): S? {
        assert(instance != null)
        if (instance is QuestionConstruct) {
            return loadQuestionConstruct(instance as QuestionConstruct?)
        } else if (instance is Sequence) {
            loadSequence(instance as Sequence)
        }
        return instance
    }

    private fun <S : ControlConstruct?> loadQuestionConstruct(instance: QuestionConstruct?): S? {
        Hibernate.initialize(instance.getControlConstructInstructions())
        //        instance.populateInstructions();                // instructions has to be unpacked into pre and post instructions
        try {
            if (instance.getQuestionItemRef() != null && instance.getQuestionItemRef()
                    .getElementId() != null && instance.getQuestionItemRef().getElement() == null
            ) {
                qiLoader.fill(instance.getQuestionItemRef())
            }
        } catch (ex: Exception) {
            LOG.error("CCS QI revision not found " + ex.message)
        }
        return instance
    }

    private fun loadSequence(sequence: Sequence) {
        if (StackTraceFilter.stackContains("getPdf", "getXml")) {
            sequence.sequence!!.forEach(Consumer<ElementRefEmbedded<ControlConstruct?>?> { seq: ElementRefEmbedded<ControlConstruct?>? ->
                seq.setElement(
                    postLoadProcessing<ControlConstruct>(
                        auditService.findRevision(seq.elementId, seq.elementRevision).getEntity()
                    )
                )
                LOG.info("PDF-XML -> fetched " + seq.getElement().name)
            })
        }
    }

    init {
        auditService = controlConstructAuditService
        this.iService = iService
        this.uService = uService
        qiLoader = ElementLoader<QuestionItem>(questionAuditService)
    }
}
