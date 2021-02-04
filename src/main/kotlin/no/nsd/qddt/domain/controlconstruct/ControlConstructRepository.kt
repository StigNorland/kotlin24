package no.nsd.qddt.domain.controlconstruct

import no.nsd.qddt.domain.classes.interfaces.BaseRepository
import no.nsd.qddt.domain.controlconstruct.pojo.ControlConstruct
import no.nsd.qddt.domain.controlconstruct.pojo.QuestionConstruct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */
@Repository
internal interface ControlConstructRepository : BaseRepository<ControlConstruct?, UUID?> {
    /**
     *
     * @param questionId
     * @return
     */
    fun findByQuestionItemRefElementId(questionId: UUID?): List<QuestionConstruct?>

    @Query(
        name = "findByQuery", nativeQuery = true, value = "SELECT cc.* FROM CONTROL_CONSTRUCT cc " +
                "LEFT JOIN AUDIT.QUESTION_ITEM_AUD qi ON qi.id = cc.questionItem_id  AND  qi.rev = cc.questionItem_revision " +
                "WHERE cc.control_construct_kind = :kind AND " +
                "cc.xml_lang ILIKE :xmlLang AND " +
                "( cc.control_construct_super_kind = :superKind or cc.name ILIKE :name or cc.description ILIKE :description " +
                "or qi.name ILIKE :questionName or qi.question ILIKE :questionText ) "
                + "ORDER BY ?#{#pageable}", countQuery = "SELECT count(cc.*) FROM CONTROL_CONSTRUCT cc " +
                "LEFT JOIN AUDIT.QUESTION_ITEM_AUD qi ON qi.id = cc.questionItem_id  AND  qi.rev = cc.questionItem_revision " +
                "WHERE cc.control_construct_kind = :kind AND " +
                "cc.xml_lang ILIKE :xmlLang AND " +
                "( cc.control_construct_super_kind = :superKind or cc.name ILIKE :name or cc.description ILIKE :description " +
                "or qi.name ILIKE :questionName or qi.question ILIKE :questionText ) "
                + " ORDER BY ?#{#pageable}"
    )
    fun <S : ControlConstruct?> findByQuery(
        @Param("kind") kind: String?, @Param("superKind") superKind: String?,
        @Param("name") name: String?, @Param("description") desc: String?,
        @Param("questionName") questionName: String?, @Param("questionText") questionText: String?,
        @Param("xmlLang") xmlLang: String?,
        pageable: Pageable?
    ): Page<S>

    @Query(
        name = "removeInstruction",
        nativeQuery = true,
        value = "DELETE FROM control_construct_instruction cci WHERE cci.control_construct_id = :controlConstructId"
    )
    fun  //                "UPDATE cci SET =2 FROM control_construct_instruction cci WHERE cci.control_construct_id = :controlConstructId")
            removeInstruction(@Param("controlConstructId") controlConstructId: UUID?)

    @Query(
        name = "removeUniverse",
        nativeQuery = true,
        value = "DELETE FROM control_construct_universe ccu WHERE ccu.question_construct_id = :controlConstructId"
    )
    fun removeUniverse(@Param("controlConstructId") controlConstructId: UUID?)
}
