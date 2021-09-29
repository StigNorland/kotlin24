package no.nsd.qddt.repository

import no.nsd.qddt.model.Category
import no.nsd.qddt.repository.projection.CategoryListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

/**
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
@RepositoryRestResource(path = "category", itemResourceRel = "Category", excerptProjection = CategoryListe::class)
interface CategoryRepository : BaseMixedRepository<Category>  {
    @Query(
        value = "SELECT ca.* FROM category ca WHERE " +
                "( ca.xml_lang ILIKE :xmlLang " +
                "AND (:categoryType is null OR ca.category_kind = cast(:categoryType AS text)) " +
                "AND (:hierarchyLevel is null OR ca.hierarchy_level ILIKE cast(:hierarchyLevel AS text)) " +
                "AND (:label is null OR ca.label ILIKE cast(:label AS text)) " +
                "AND (:name is null OR ca.name ILIKE cast(:name AS text)) " +
                "AND (:description is null OR ca.description ILIKE cast(:description AS text)) )"
        ,
        countQuery = "SELECT count(ca.*) FROM category ca WHERE " +
                "( ca.xml_lang ILIKE :xmlLang " +
                "AND (:categoryType is null OR ca.category_kind = cast(:categoryType AS text)) " +
                "AND (:hierarchyLevel is null OR ca.hierarchy_level ILIKE cast(:hierarchyLevel AS text)) " +
                "AND (:label is null OR ca.label ILIKE cast(:label AS text)) " +
                "AND (:name is null OR ca.name ILIKE cast(:name AS text)) " +
                "AND (:description is null OR ca.description ILIKE cast(:description AS text)) )"
                ,
        nativeQuery = true
    )
    fun findByQuery(
        @Param("categoryType") categoryType: String?,
        @Param("hierarchyLevel") hierarchyLevel: String?,
        @Param("name") name: String?,
        @Param("label") label: String?,
        @Param("description") description: String?,
        @Param("xmlLang") xmlLang: String?,
        pageable: Pageable?
    ): Page<Category?>?
}
