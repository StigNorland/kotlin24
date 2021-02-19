package no.nsd.qddt.repository

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.interfaces.BaseRepository
import no.nsd.qddt.repository.projection.CategoryListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import java.util.*

/**
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
@RepositoryRestResource(path = "categories", collectionResourceRel = "Categories", itemResourceRel = "Category", excerptProjection = CategoryListe::class)
internal interface CategoryRepository : BaseRepository<Category, UUID>  {
    @Query(
        value = "SELECT ca.* FROM category ca WHERE ( ca.category_kind ILIKE :categoryType OR ca.hierarchy_level ILIKE :hierarchyLevel ) " +
                "AND ( ca.xml_lang ILIKE :xmlLang AND (ca.name LIKE :name or ca.label ILIKE :name  or ca.description ILIKE :description ) )" +
                "ORDER BY ?#{#pageable}",
        countQuery = "SELECT count(ca.*) FROM category ca WHERE ( ca.category_kind ILIKE :categoryType OR ca.hierarchy_level ILIKE :hierarchyLevel ) " +
                "AND ( ca.xml_lang ILIKE :xmlLang AND (ca.name LIKE :name or ca.label ILIKE :name  or ca.description ILIKE :description ) )"
                + " ORDER BY ?#{#pageable}",
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
