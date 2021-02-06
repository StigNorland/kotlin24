package no.nsd.qddt.domain.universe;

import no.nsd.qddt.domain.classes.interfaces.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "universes", collectionResourceRel = "universe", itemResourceRel = "Universe", excerptProjection = UniverseListe::class)
open interface UniverseRepository : JpaRepository<Universe, UUID> {

    @RestResource(rel = "desctiption", path = "findBy")
    fun findByDescriptionIgnoreCaseLikeAndXmlLangLike(String description, String xmlLang,  Pageable pageable): Page<Universe>

}

