package no.nsd.qddt.domain.controlconstruct.audit

import no.nsd.qddt.domain.category.Category
import no.nsd.qddt.domain.controlconstruct.pojo.Sequence
import no.nsd.qddt.domain.instrument.pojo.Parameter
import no.nsd.qddt.utils.FilterTool.defaultSort
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.regex.Pattern

/**
 * @author Dag Østgulen Heradstveit
 */
@Service("instrumentAuditQuestionService")
internal class ControlConstructAuditServiceImpl @Autowired constructor(
    private val controlConstructAuditRepository: ControlConstructAuditRepository,
    qAuditService: QuestionItemAuditService?
) : AbstractAuditFilter<Int?, ControlConstruct?>(), ControlConstructAuditService {
    protected val LOG = LoggerFactory.getLogger(this.javaClass)
    private val qidLoader: ElementLoader<QuestionItem>
    private var showPrivateComments = false
    @Transactional(readOnly = true)
    override fun findLastChange(id: UUID): Revision<Int, ControlConstruct>? {
        return postLoadProcessing(controlConstructAuditRepository.findLastChangeRevision(id).get())
    }

    @Transactional(readOnly = true)
    override fun findRevision(id: UUID, revision: Int): Revision<Int, ControlConstruct>? {
        return postLoadProcessing(controlConstructAuditRepository.findRevision(id, revision).get())
    }

    @Transactional(readOnly = true)
    override fun findRevisions(id: UUID, pageable: Pageable?): Page<Revision<Int, ControlConstruct>?>? {
        return controlConstructAuditRepository.findRevisions(id, pageable!!)
            .map<Revision<Int, ControlConstruct>?> { instance: Revision<Int?, ControlConstruct?>? ->
                this.postLoadProcessing(
                    instance
                )
            }
    }

    override fun findFirstChange(uuid: UUID): Revision<Int, ControlConstruct>? {
        val pageable: PageRequest = PageRequest.of(0, 1)
        return postLoadProcessing(
            controlConstructAuditRepository.findRevisions(
                uuid,
                defaultSort(pageable, "RevisionNumber DESC")
            ).content[0]
        )
    }

    override fun setShowPrivateComment(showPrivate: Boolean) {
        showPrivateComments = showPrivate
    }

    override fun findRevisionsByChangeKindNotIn(
        id: UUID,
        changeKinds: Collection<ChangeKind?>?,
        pageable: Pageable?
    ): Page<Revision<Int?, ControlConstruct?>?> {
        return getPage(controlConstructAuditRepository.findRevisions(id), changeKinds, pageable)
    }

    protected override fun postLoadProcessing(instance: Revision<Int, ControlConstruct>?): Revision<Int, ControlConstruct>? {
        instance.getEntity().version.revision = instance.getRevisionNumber().get()
        return Revision.of(instance.getMetadata(), postLoadProcessing(instance.getEntity()))
    }

    private fun postLoadProcessing(instance: ControlConstruct?): ControlConstruct {
        assert(instance != null)
        Hibernate.initialize(instance.getOtherMaterials())
        return if (instance.classKind == "QUESTION_CONSTRUCT") postLoadProcessing(instance as QuestionConstruct?) else if (instance.classKind == "CONDITION_CONSTRUCT") postLoadProcessing(
            instance as ConditionConstruct?
        ) else if (instance.classKind == "STATEMENT_CONSTRUCT") postLoadProcessing(instance as StatementItem?) else instance
    }

    private val TAGS = Pattern.compile("\\[(.{1,25}?)]")

    /*
    post fetch processing, some elements are not supported by the framework (enver mixed with jpa db queries)
    thus we need to populate some elements ourselves.
     */
    private fun postLoadProcessing(instance: QuestionConstruct?): QuestionConstruct? {
        assert(instance != null)
        try {
            // FIX BUG instructions doesn't load within ControlConstructAuditServiceImpl, by forcing read here, it works...
            // https://github.com/DASISH/qddt-client/issues/350
            Hibernate.initialize(instance.getControlConstructInstructions())
            instance.getControlConstructInstructions()
                .forEach(Consumer<ControlConstructInstruction> { element: ControlConstructInstruction ->
                    LOG.info(
                        "LOADING FINE " + element.getInstruction().getDescription()
                    )
                })
            if (instance.getQuestionItemRef().getElementId() != null) {
                qidLoader.fill(instance.getQuestionItemRef())
                val question: String = instance.getQuestionItemRef().getText()
                var matcher = TAGS.matcher(question)
                while (matcher.find()) {
                    instance.getParameterIn().add(Parameter(matcher.group(1).toUpperCase(), "IN"))
                }
                val rd: ResponseDomain = instance.getQuestionItemRef().getElement().getResponseDomainRef().getElement()
                val rds: String = rd.getManagedRepresentationFlatten().stream()
                    .filter(Predicate { category: Category -> category.getCategoryType() !== CategoryType.MIXED })
                    .map(Function { obj: Category -> obj.getLabel() }).collect(Collectors.joining(" "))
                matcher = TAGS.matcher(rds)
                while (matcher.find()) {
                    instance.getParameterIn().add(Parameter(matcher.group(1).toUpperCase(), "IN"))
                }
                val parameterOuts: MutableSet<Parameter> = HashSet()
                parameterOuts.add(Parameter(instance.name, "OUT"))
                instance.setParameterOut(parameterOuts)
            }
        } catch (ex: Exception) {
            LOG.error("removeQuestionItem", ex)
        }
        return instance
    }

    private fun postLoadProcessing(instance: ConditionConstruct): ConditionConstruct {
        try {
            val matcher = TAGS.matcher(instance.getCondition())
            while (matcher.find()) {
                LOG.info("postLoadProcessing::MATCH: " + matcher.group(1))
                instance.getParameterIn().add(Parameter(matcher.group(1).toUpperCase(), "IN"))
            }
            val parameterOuts: MutableSet<Parameter> = HashSet()
            parameterOuts.add(Parameter(instance.name, "OUT"))
            instance.setParameterOut(parameterOuts)
        } catch (ex: Exception) {
            LOG.error("postLoadProcessing", ex)
        }
        return instance
    }

    private fun postLoadProcessing(instance: StatementItem): StatementItem {
        val matcher = TAGS.matcher(instance.getStatement())
        while (matcher.find()) {
            instance.getParameterIn().add(Parameter(matcher.group(1).toUpperCase(), "IN"))
        }
        return instance
    }

    private fun postLoadProcessing(instance: Sequence): Sequence {

//          instance.getSequence()
        return instance
    }

    init {
        qidLoader = ElementLoader<QuestionItem>(qAuditService)
    }
}
