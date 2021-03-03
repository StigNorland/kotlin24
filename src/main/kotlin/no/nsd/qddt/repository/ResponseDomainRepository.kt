package no.nsd.qddt.repository
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.repository.projection.ResponseDomainListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
* @author Dag Østgulen Heradstveit
* @author Stig Norland
*/
@RepositoryRestResource(path = "responsedomain",  itemResourceRel = "ResponseDomain", excerptProjection = ResponseDomainListe::class)
interface ResponseDomainRepository:  RevisionRepository<ResponseDomain, UUID, Int>, JpaRepository<ResponseDomain, UUID>  {


  // Page<ResponseDomain> findByResponseKindAndNameIgnoreCaseLikeOrDescriptionIgnoreCaseLike(ResponseKind responseKind, String name, String description, Pageable pageable);
  // TODO fix query --->



  @Query( nativeQuery = true,
      value = ("SELECT RD.* FROM RESPONSEDOMAIN RD " +
              " WHERE RD.response_kind = :ResponseKind AND  ( " +
              "   (:xmlLang is null OR RD.xml_lang ILIKE cast(:xmlLang AS text)) AND ( " +
              "     (:name is null OR RD.name ILIKE cast(:name AS text)) " +
              "     OR (:description is null OR RD.description ILIKE cast(:description AS text)) " +
              "     OR RD.category_id in (select distinct c.id FROM category c WHERE  (:anchor is null OR c.description ILIKE cast(:anchor AS text)) ) " +
              "     OR RD.id in (select distinct qi.responsedomain_id FROM question_item qi WHERE  (:name is null OR qi.name ILIKE cast(:name AS text)) " +
              "         OR (:question is null OR qi.question ILIKE cast(:question AS text)) ) " +
              "   )" +
              " )"
      ),
      countQuery = ("SELECT count(RD.*) FROM RESPONSEDOMAIN RD " +
              " WHERE RD.response_kind = :ResponseKind AND  ( " +
              "   (:xmlLang is null OR RD.xml_lang ILIKE cast(:xmlLang AS text)) AND ( " +
              "     (:name is null OR RD.name ILIKE cast(:name AS text)) " +
              "     OR (:description is null OR RD.description ILIKE cast(:description AS text)) " +
              "     OR RD.category_id in (select distinct c.id FROM category c WHERE  (:anchor is null OR c.description ILIKE cast(:anchor AS text)) ) " +
              "     OR RD.id in (select distinct qi.responsedomain_id FROM question_item qi WHERE  (:name is null OR qi.name ILIKE cast(:name AS text)) " +
              "         OR (:question is null OR qi.question ILIKE cast(:question AS text)) ) " +
              "   )" +
              " )"
      )
  )
  fun findByQuery(@Param("ResponseKind") ResponseKind:String,
                  @Param("name") name:String?="%",
                  @Param("description") description:String?,
                  @Param("question") question:String?,
                  @Param("anchor") anchor:String?,
                  @Param("xmlLang") xmlLang:String?,
                  pageable:Pageable?= Pageable.unpaged()):Page<ResponseDomain>
}
