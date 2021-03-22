package no.nsd.qddt.repository

import no.nsd.qddt.model.PublicationStatus
import no.nsd.qddt.repository.projection.PublicationStatusListe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */

@RepositoryRestResource(path = "publicationstatus", exported = false , itemResourceRel = "PublicationStatus", excerptProjection = PublicationStatusListe::class)
interface PublicationStatusRepository : JpaRepository<PublicationStatus, Long> {


}
