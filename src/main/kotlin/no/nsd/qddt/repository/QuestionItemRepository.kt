package no.nsd.qddt.repository
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.repository.projection.QuestionItemListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
* @author Stig Norland
*/
@RepositoryRestResource(path = "questionitem",  itemResourceRel = "QuestionItem", excerptProjection = QuestionItemListe::class)
interface QuestionItemRepository:  JpaRepository<QuestionItem, UUID>,  RevisionRepository<QuestionItem, UUID, Int>  {

    @Query(nativeQuery = true,
    value = ("SELECT qi.* FROM QUESTION_ITEM qi " +
            " LEFT JOIN audit.responsedomain_aud r on qi.responsedomain_id = r.id and qi.responsedomain_revision = r.rev " +
            " WHERE qi.xml_lang ILIKE :xmlLang " +
            " AND ( (:name is null OR qi.name ILIKE cast(:name AS text)) " +
            " OR (:question is null OR qi.question ILIKE cast(:question AS text)) " +
            " OR (:responseDomain is null OR r.name ILIKE cast(:responseDomain AS text)) " +
            " ) "
        ),
    countQuery = ("SELECT count(qi.id) FROM QUESTION_ITEM qi " +
                " LEFT JOIN audit.responsedomain_aud r on qi.responsedomain_id = r.id and qi.responsedomain_revision = r.rev " +
                " WHERE qi.xml_lang ILIKE :xmlLang " +
                " AND ( " +
                " (:name is null OR qi.name ILIKE cast(:name AS text)) " +
                " OR (:question is null OR qi.question ILIKE cast(:question AS text)) " +
                " OR (:responseDomain is null OR r.name ILIKE cast(:responseDomain AS text)) " +
                " ) "
        ),
    )
    fun findByQuery(name:String?,question:String?,responseDomain: String?,xmlLang:String?="en-GB",pageable:Pageable?):Page<QuestionItem>?

}
