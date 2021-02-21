package no.nsd.qddt.repository;


import no.nsd.qddt.model.Universe
import no.nsd.qddt.repository.projection.UniverseListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "universe", collectionResourceRel = "Universes", itemResourceRel = "Universe", excerptProjection = UniverseListe::class)
interface UniverseRepository : JpaRepository<Universe,UUID> {

    @RestResource(rel = "desctiption", path = "findBy")
    fun findByDescriptionIgnoreCaseLikeAndXmlLangLike( description: String, xmlLang: String,  pageable: Pageable): Page<Universe>

}

