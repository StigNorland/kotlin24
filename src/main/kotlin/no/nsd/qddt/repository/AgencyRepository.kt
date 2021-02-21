package no.nsd.qddt.repository

import no.nsd.qddt.model.Agency
import no.nsd.qddt.repository.projection.AgencyListe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */

@RepositoryRestResource(path = "agency", collectionResourceRel = "Agencies", itemResourceRel = "Agency", excerptProjection = AgencyListe::class)
interface AgencyRepository : JpaRepository<Agency,UUID>
