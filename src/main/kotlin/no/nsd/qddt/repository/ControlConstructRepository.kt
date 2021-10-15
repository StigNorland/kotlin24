package no.nsd.qddt.repository
import no.nsd.qddt.model.ControlConstruct
import no.nsd.qddt.model.QuestionConstruct
import no.nsd.qddt.repository.projection.ControlConstructListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
* @author Stig Norland
*/
@RepositoryRestResource(path = "controlconstruct",  itemResourceRel = "ControlConstruct", excerptProjection = ControlConstructListe::class)
interface ControlConstructRepository<T : ControlConstruct>:BaseMixedRepository<T>{

        /**
         *
         * @param questionId
         * @return
         */
        fun findByQuestionIdId(questionId: UUID): List<QuestionConstruct>?


        @Query(
            value = "SELECT cc.* FROM CONTROL_CONSTRUCT cc " +
                    "LEFT JOIN AUDIT.QUESTION_ITEM_AUD qi ON qi.id = cc.questionItem_id  AND  qi.rev = cc.questionItem_revision " +
                    "WHERE cc.control_construct_kind = cast(:constructKind AS text) " +
                    "AND cc.xml_lang ILIKE :xmlLang " +
                    "AND( (:superKind is null OR cc.control_construct_super_kind = cast(:superKind AS text))  " +
                    "AND ( cc.label ILIKE searchStr(:label) or cc.name ILIKE searchStr(:name) or cc.description ILIKE searchStr(:description)) " +
                    "OR (:questionName is null OR qi.name ILIKE cast(:questionName AS text)) " +
                    "OR (:questionText is null OR qi.question ILIKE cast(:questionText AS text)) " +
                    ") ",
            countQuery = "SELECT count(cc.*) FROM CONTROL_CONSTRUCT cc " +
                    "LEFT JOIN AUDIT.QUESTION_ITEM_AUD qi ON qi.id = cc.questionItem_id  AND  qi.rev = cc.questionItem_revision " +
                    "WHERE cc.control_construct_kind =  cast(:constructKind AS text) " +
                    " AND cc.xml_lang ILIKE :xmlLang " +
                    " AND( (:superKind is null OR cc.control_construct_super_kind = cast(:superKind AS text))  " +
                    " AND ( cc.label ILIKE searchStr(:label) or cc.name ILIKE searchStr(:name) or cc.description ILIKE searchStr(:description)) " +
                    " OR (:questionName is null OR qi.name ILIKE cast(:questionName AS text)) " +
                    " OR (:questionText is null OR qi.question ILIKE cast(:questionText AS text)) " +
                    " ) ",
            nativeQuery = true
        )
        fun <S : ControlConstruct?> findByQuery(
            @Param("constructKind") constructKind: String,
            @Param("superKind") superKind: String?,
            @Param("label") label: String?,
            @Param("name") name: String?,
            @Param("description") desc: String?,
            @Param("questionName") questionName: String?,
            @Param("questionText") questionText: String?,
            @Param("xmlLang") xmlLang: String?="%",
            pageable: Pageable?
        ): Page<S>?


        @Query(
            name = "removeInstruction", nativeQuery = true,
            value = "DELETE FROM control_construct_instruction cci WHERE cci.control_construct_id = :controlConstructId"
        )
        fun removeInstruction(@Param("controlConstructId") controlConstructId: UUID?)

        @Query(
            name = "removeUniverse", nativeQuery = true,
            value = "DELETE FROM control_construct_universe ccu WHERE ccu.question_construct_id = :controlConstructId"
        )
        fun removeUniverse(@Param("controlConstructId") controlConstructId: UUID?)

    }

