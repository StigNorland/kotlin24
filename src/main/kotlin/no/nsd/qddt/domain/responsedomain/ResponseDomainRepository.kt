package no.nsd.qddt.domain.responsedomain
import no.nsd.qddt.domain.classes.interfaces.BaseRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID
/**
* @author Dag Østgulen Heradstveit
* @author Stig Norland
*/
@Repository
internal interface ResponseDomainRepository:BaseRepository<ResponseDomain, UUID> {
  // Page<ResponseDomain> findByResponseKindAndNameIgnoreCaseLikeOrDescriptionIgnoreCaseLike(ResponseKind responseKind, String name, String description, Pageable pageable);
  // TODO fix query --->
  @Query(value = ("SELECT RD.* FROM RESPONSEDOMAIN RD WHERE RD.response_kind = :responseKind AND " +
                  "(RD.xml_lang ILIKE :xmlLang AND ( RD.name ILIKE :name or RD.description ILIKE :description OR " +
                  "RD.category_id in (select distinct c.id FROM category c WHERE c.description ILIKE :anchor ) OR " +
                  "RD.id in (select distinct qi.responsedomain_id FROM question_item qi WHERE qi.name ILIKE :question OR qi.question ILIKE :question ) ) )"
                  + " ORDER BY ?#{#pageable}"), countQuery = ("SELECT count(RD.*) FROM RESPONSEDOMAIN RD WHERE RD.response_kind = :responseKind AND " +
                                                              "(RD.xml_lang ILIKE :xmlLang AND ( RD.name ILIKE :name or RD.description ILIKE :description OR " +
                                                              "RD.category_id in (select distinct c.id FROM category c WHERE c.description ILIKE :anchor ) OR " +
                                                              "RD.id in (select distinct qi.responsedomain_id FROM question_item qi WHERE qi.name ILIKE :question OR qi.question ILIKE :question ) ) )"
                                                              + " ORDER BY ?#{#pageable}"), nativeQuery = true)
  fun findByQuery(@Param("responseKind") responseKind:String,
                  @Param("name") name:String,
                  @Param("description") description:String,
                  @Param("question") question:String,
                  @Param("anchor") anchor:String,
                  @Param("xmlLang") xmlLang:String,
                  pageable:Pageable):Page<ResponseDomain>
}