package no.nsd.qddt.repository

import no.nsd.qddt.model.Instruction
import no.nsd.qddt.model.PublicationStatus
import no.nsd.qddt.repository.projection.PublicationStatusListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.*

/**
 * @author Stig Norland
 */

@RepositoryRestResource(path = "publicationstatus",  itemResourceRel = "PublicationStatus", excerptProjection = PublicationStatusListe::class)
interface PublicationStatusRepository : JpaRepository<PublicationStatus, Int> {

    @RestResource(rel = "hierarchy", path = "/all")
    fun findAllByParentIdIsNull(): List<PublicationStatus>

}
