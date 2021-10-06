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
                "AND (:categoryKind is null OR ca.category_kind = cast(:categoryKind AS text)) " +
                "AND (:hierarchyLevel is null OR ca.hierarchy_level ILIKE cast(:hierarchyLevel AS text)) " +
                "AND ( ca.label ILIKE searchStr(:label) " +
                "OR ca.name  ILIKE searchStr(:name) " +
                "OR ca.description ILIKE searchStr(:description)))" ,
        countQuery = "SELECT count(ca.*) FROM category ca WHERE " +
                "( ca.xml_lang ILIKE :xmlLang " +
                "AND (:categoryKind is null OR ca.category_kind = cast(:categoryKind AS text)) " +
                "AND (:hierarchyLevel is null OR ca.hierarchy_level ILIKE cast(:hierarchyLevel AS text)) " +
                "AND ( ca.label ILIKE searchStr(:label) " +
                "OR ca.name  ILIKE searchStr(:name) " +
                "OR ca.description ILIKE searchStr(:description)))" ,
        nativeQuery = true
    )
    fun findByQuery(
        @Param("xmlLang") xmlLang: String?,
        @Param("categoryKind") categoryKind: String?,
        @Param("hierarchyLevel") hierarchyLevel: String?,
        @Param("label") label: String?,
        @Param("name") name: String?,
        @Param("description") description: String?,
        pageable: Pageable?
    ): Page<Category?>?
}
