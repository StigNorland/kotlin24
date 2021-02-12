package no.nsd.qddt.repository;

import no.nsd.qddt.repository.projection.AgencyListe
import no.nsd.qddt.model.Agency
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */

@RepositoryRestResource(path = "agencies", collectionResourceRel = "agency", itemResourceRel = "agency", excerptProjection = AgencyListe::class)
interface AgencyRepository : JpaRepository<Agency, UUID>
