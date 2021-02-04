package no.nsd.qddt.domain.controlconstruct

import no.nsd.qddt.domain.classes.interfaces.BaseService
import no.nsd.qddt.domain.controlconstruct.json.ConstructJsonView
import no.nsd.qddt.domain.controlconstruct.json.ConstructQuestionJson
import no.nsd.qddt.domain.controlconstruct.pojo.ControlConstruct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * @author Dag Østgulen Heradstveit
 */
interface ControlConstructService : BaseService<ControlConstruct?, UUID?> {
    /**
     * @param questionItemIds
     * @return
     */
    fun findByQuestionItems(questionItemIds: List<UUID?>): List<ConstructQuestionJson?>?
    fun <S : ConstructJsonView?> findBySearcAndControlConstructKind(
        kind: String?,
        superKind: String,
        name: String,
        description: String,
        xmlLang: String?,
        pageable: Pageable?
    ): Page<S>

    fun <S : ConstructJsonView?> findQCBySearch(
        name: String,
        questionName: String,
        questionText: String,
        xmlLang: String?,
        pageable: Pageable?
    ): Page<S>
}
