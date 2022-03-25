package no.nsd.qddt.repository
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.repository.projection.QuestionItemListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

/**
* @author Stig Norland
*/
@RepositoryRestResource(path = "questionitem",  itemResourceRel = "QuestionItem", excerptProjection = QuestionItemListe::class)
interface QuestionItemRepository:  BaseEntityAuditRepository<QuestionItem>  {

    @Query(nativeQuery = true,
    value =
        "SELECT qi.* FROM QUESTION_ITEM qi " +
        "LEFT JOIN audit.responsedomain_aud r on qi.responsedomain_id = r.id and qi.responsedomain_revision = r.rev " +
        "WHERE qi.xml_lang ILIKE :xmlLang " +
        "AND ( " +
        " qi.name ILIKE searchStr(cast(:name AS text)) " +
        " or qi.question ILIKE searchStr(cast(:question AS text)) " +
        " or r.name ILIKE searchStr(cast(:responseDomain AS text)) " +
        ")",
    countQuery =
        "SELECT count(qi.id) FROM QUESTION_ITEM qi " +
        " LEFT JOIN audit.responsedomain_aud r on qi.responsedomain_id = r.id and qi.responsedomain_revision = r.rev " +
        " WHERE qi.xml_lang ILIKE :xmlLang " +
        "AND ( " +
        " qi.name ILIKE searchStr(cast(:name AS text)) " +
        " or qi.question ILIKE searchStr(cast(:question AS text)) " +
        " or r.name ILIKE searchStr(cast(:responseDomain AS text)) " +
        ")"
    )
    fun findByQuery(
        @Param("xmlLang") xmlLang:String,
        @Param("name") name:String?,
        @Param("question") question:String?,
        @Param("responseDomain") responseDomain:String?,
        pageable:Pageable?
    ):Page<QuestionItem>


}
