package no.nsd.qddt.domain.universe;

import no.nsd.qddt.domain.classes.interfaces.BaseRepository;
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.*



import java.util.UUID;

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "universes", collectionResourceRel = "universe", itemResourceRel = "Universe", excerptProjection = UniverseListe::class)
interface UniverseRepository : JpaRepository<Universe, UUID> {

    @RestResource(rel = "desctiption", path = "findBy")
    fun findByDescriptionIgnoreCaseLikeAndXmlLangLike( description: String, xmlLang: String,  pageable: Pageable): Page<Universe>

}

