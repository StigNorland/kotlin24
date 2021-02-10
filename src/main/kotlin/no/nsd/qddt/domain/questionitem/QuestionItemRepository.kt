package no.nsd.qddt.domain.questionitem
import no.nsd.qddt.classes.interfaces.BaseRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID
/**
* @author Stig Norland
*/
@Repository
interface QuestionItemRepository:BaseRepository<QuestionItem, UUID> {

  @Query(
    value = ("SELECT qi.* FROM QUESTION_ITEM qi " +
                  "LEFT JOIN audit.responsedomain_aud r on qi.responsedomain_id = r.id and qi.responsedomain_revision = r.rev " +
                  "WHERE ( qi.xml_lang ILIKE :xmlLang AND (qi.name ILIKE :name or qi.question ILIKE :question or r.name ILIKE :responseDomain )) "
                  + "ORDER BY ?#{#pageable}"), 
    countQuery = ("SELECT count(qi.id) FROM QUESTION_ITEM qi " +
                    "LEFT JOIN audit.responsedomain_aud r on qi.responsedomain_id = r.id and qi.responsedomain_revision = r.rev " +
                    "WHERE ( qi.xml_lang ILIKE :xmlLang AND (qi.name ILIKE :name or qi.question ILIKE :question or r.name ILIKE :responseDomain )) "
                    + "ORDER BY ?#{#pageable}"),
    nativeQuery = true)
  fun findByNames(@Param("name") name:String,
                  @Param("question") question:String,
                  @Param("responseDomain") responseDomain:String,
                  @Param("xmlLang") xmlLang:String,
                  pageable:Pageable):Page<QuestionItem>
}
