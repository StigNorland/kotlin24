package no.nsd.qddt.repository

import no.nsd.qddt.model.Category
import no.nsd.qddt.repository.projection.CategoryListe
import no.nsd.qddt.repository.projection.ManagedRepresentation
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

    @Query(nativeQuery = true,
        value = """
    SELECT ca.* FROM category ca 
    WHERE ( ca.xml_lang ILIKE :xmlLang 
        AND (:categoryKind is null OR ca.category_kind = cast(:categoryKind AS text)) 
        AND (ca.name ILIKE searchStr(cast(:name AS text))
        OR ca.label ILIKE searchStr(cast(:label AS text))
        OR ca.description ILIKE searchStr(cast(:description AS text)) )) """,
        countQuery = """
    SELECT count(ca.*) FROM category ca 
    WHERE ( ca.xml_lang ILIKE :xmlLang 
        AND (:categoryKind is null OR ca.category_kind = cast(:categoryKind AS text)) 
        AND (ca.name ILIKE searchStr(cast(:name AS text))
        OR ca.label ILIKE searchStr(cast(:label AS text))
        OR ca.description ILIKE searchStr(cast(:description AS text)) )) """
    )
    fun findByQuery(
        @Param("xmlLang") xmlLang: String?,
        @Param("categoryKind") categoryKind: String?,
        @Param("label") label: String? ="*",
        @Param("name") name: String? ="*",
        @Param("description") description: String? ="*",
        pageable: Pageable?
    ): Page<Category>

    @Query(nativeQuery = true,
        value = """
    SELECT ca.* FROM category ca 
    WHERE ( ca.xml_lang ILIKE :xmlLang 
        AND ca.hierarchy_level = 'GROUP_ENTITY'
        AND (:categoryKind is null OR ca.category_kind = cast(:categoryKind AS text)) 
        AND (ca.name ILIKE searchStr(cast(:name AS text))
        OR ca.label ILIKE searchStr(cast(:label AS text))
        OR ca.description ILIKE searchStr(cast(:description AS text)) )) """,
        countQuery = """
    SELECT count(ca.*) FROM category ca 
    WHERE ( ca.xml_lang ILIKE :xmlLang 
        AND ca.hierarchy_level = 'GROUP_ENTITY'
        AND (:categoryKind is null OR ca.category_kind = cast(:categoryKind AS text)) 
        AND (ca.name ILIKE searchStr(cast(:name AS text))
        OR ca.label ILIKE searchStr(cast(:label AS text))
        OR ca.description ILIKE searchStr(cast(:description AS text)) )) """
    )
    fun findByManagedQuery(
        @Param("xmlLang") xmlLang: String?,
        @Param("categoryKind") categoryKind: String?,
        @Param("label") label: String? ="*",
        @Param("name") name: String? ="*",
        @Param("description") description: String? ="*",
        pageable: Pageable?
    ): Page<ManagedRepresentation>

//    fun <T> findByLastName(lastName: String?, type: Class<T>?): T
}
