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
        value = "SELECT ca.* FROM category ca WHERE ( ca.category_kind ILIKE :categoryType OR ca.hierarchy_level ILIKE :hierarchyLevel ) " +
                "AND ( ca.xml_lang ILIKE :xmlLang AND (ca.name LIKE :name or ca.label ILIKE :name  or ca.description ILIKE :description ) )"
                ,
        countQuery = "SELECT count(ca.*) FROM category ca WHERE ( ca.category_kind ILIKE :categoryType OR ca.hierarchy_level ILIKE :hierarchyLevel ) " +
                "AND ( ca.xml_lang ILIKE :xmlLang AND (ca.name LIKE :name or ca.label ILIKE :name  or ca.description ILIKE :description ) )"
                ,
        nativeQuery = true
    )
    fun findByQuery(
        @Param("categoryType") categoryType: String?,
        @Param("hierarchyLevel") hierarchyLevel: String?,
        @Param("name") name: String?,
        @Param("description") description: String?,
        @Param("xmlLang") xmlLang: String?,
        pageable: Pageable?
    ): Page<Category?>?
}
