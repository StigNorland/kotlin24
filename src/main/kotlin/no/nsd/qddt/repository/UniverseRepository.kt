package no.nsd.qddt.repository


import no.nsd.qddt.model.Universe
import no.nsd.qddt.repository.projection.UniverseListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "universe",  itemResourceRel = "Universe", excerptProjection = UniverseListe::class)
interface UniverseRepository : JpaRepository<Universe,UUID> {

    @Query(
        value = "SELECT ca.* FROM universe ca WHERE " +
                "( ca.xml_lang ILIKE :xmlLang " +
                "AND ca.description ILIKE searchStr(:description))" ,
        countQuery = "SELECT count(ca.*) FROM universe ca WHERE " +
                "( ca.xml_lang ILIKE :xmlLang " +
                "AND ca.description ILIKE searchStr(:description))" ,
        nativeQuery = true
    )
    fun findByQuery(
        @Param("xmlLang") xmlLang: String?,
        @Param("description") description: String?,
        pageable: Pageable?
    ): Page<Universe>

//    @RestResource(rel = "description", path = "findByQuery")
//    fun findByDescriptionIgnoreCaseLikeAndXmlLangLike( description: String, xmlLang: String,  pageable: Pageable): Page<Universe>

}

