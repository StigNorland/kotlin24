package no.nsd.qddt.repository
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.interfaces.BaseRepository
import no.nsd.qddt.repository.projection.QuestionItemListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
* @author Stig Norland
*/
@RepositoryRestResource(path = "questionitem", collectionResourceRel = "QuestionItems", itemResourceRel = "QuestionItem", excerptProjection = QuestionItemListe::class)
interface QuestionItemRepository:BaseRepository<QuestionItem, UUID> {

    @Query(
    value = ("SELECT qi.* FROM QUESTION_ITEM qi " +
                  "LEFT JOIN audit.responsedomain_aud r on qi.responsedomain_id = r.id and qi.responsedomain_revision = r.rev " +
                  "WHERE ( qi.xml_lang ILIKE :xmlLang AND (qi.name ILIKE :name or qi.question ILIKE :question or r.name ILIKE :responseDomain )) "
//                  + "ORDER BY ?#{#pageable}"
            ),
    countQuery = ("SELECT count(qi.id) FROM QUESTION_ITEM qi " +
                    "LEFT JOIN audit.responsedomain_aud r on qi.responsedomain_id = r.id and qi.responsedomain_revision = r.rev " +
                    "WHERE ( qi.xml_lang ILIKE :xmlLang AND (qi.name ILIKE :name or qi.question ILIKE :question or r.name ILIKE :responseDomain )) "
//                    + "ORDER BY ?#{#pageable}"
            ),
    nativeQuery = true)
  fun findByQuery(name:String?="%",
                  question:String?="%",
                  responseDomain: String?="%",
                  xmlLang:String?="en-GB",
                  pageable:Pageable):Page<QuestionItem>

}
