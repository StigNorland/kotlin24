package no.nsd.qddt.repository
import no.nsd.qddt.model.ControlConstruct
import no.nsd.qddt.repository.projection.ControlConstructListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
* @author Stig Norland
*/
@RepositoryRestResource(path = "controlconstruct",  itemResourceRel = "ControlConstruct", excerptProjection = ControlConstructListe::class)
interface ControlConstructRepository<T : ControlConstruct>:BaseMixedRepository<T>{


        @Query( nativeQuery = true,
                value ="""
            SELECT cc.*  FROM CONTROL_CONSTRUCT cc
            LEFT JOIN AUDIT.QUESTION_ITEM_AUD qi ON qi.id = cc.questionItem_id AND qi.rev = cc.questionItem_revision
            WHERE cc.control_construct_kind = :constructKind 
                AND cc.xml_lang ILIKE :xmlLang
                AND ( :superKind IS NULL OR cc.CONTROL_CONSTRUCT_SUPER_KIND = cast(:superKind as TEXT) )
                AND ( cc.label ILIKE searchStr(cast(:label AS TEXT))
                    OR cc.name ILIKE searchStr(cast(:name AS TEXT))
                    OR cc.description ILIKE searchStr(cast(:description AS TEXT))
                    OR (qi.name is null or  qi.name ILIKE searchStr(cast(:questionName AS TEXT)))
                    OR (qi.question is null or qi.question ILIKE searchStr(cast(:questionText AS TEXT))) )                
""",
            countQuery = """
            SELECT count(cc.*) FROM CONTROL_CONSTRUCT cc
            LEFT JOIN AUDIT.QUESTION_ITEM_AUD qi ON qi.id = cc.questionItem_id AND qi.rev = cc.questionItem_revision
            WHERE cc.control_construct_kind = :constructKind 
                AND cc.xml_lang ILIKE :xmlLang
                AND ( :superKind IS NULL OR cc.control_construct_super_kind = cast(:superKind as TEXT) )
                AND ( cc.label ILIKE searchStr(cast(:label AS TEXT))
                    OR cc.name ILIKE searchStr(cast(:name AS TEXT))
                    OR cc.description ILIKE searchStr(cast(:description AS TEXT))
                    OR (qi.name is null or  qi.name ILIKE searchStr(cast(:questionName AS TEXT)))
                    OR (qi.question is null or qi.question ILIKE searchStr(cast(:questionText AS TEXT))) )                
"""
        )
        fun <S : ControlConstruct> findByQuery(
            @Param("constructKind") constructKind: String,
            @Param("xmlLang") xmlLang: String,
            @Param("superKind") superKind: String?,
            @Param("label") label: String?="*",
            @Param("name") name: String?="*",
            @Param("description") description: String?="*",
            @Param("questionName") questionName: String?="*",
            @Param("questionText") questionText: String?="*",
            pageable: Pageable
        ): Page<S>


        @Modifying
        @Query(
            nativeQuery = true,
            value = "DELETE FROM control_construct_instruction cci WHERE cci.control_construct_id = :controlConstructId"
        )
        fun removeInstruction(@Param("controlConstructId") controlConstructId: UUID?)

        @Modifying
        @Query(
            nativeQuery = true,
            value = "DELETE FROM control_construct_universe ccu WHERE ccu.question_construct_id = :controlConstructId"
        )
        fun removeUniverse(@Param("controlConstructId") controlConstructId: UUID?)

    }


