package no.nsd.qddt.domain.agency;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */

@RepositoryRestResource(path = "agencies", collectionResourceRel = "agency", itemResourceRel = "agency", excerptProjection = AgencyListe::class)
interface AgencyRepository : JpaRepository<Agency, UUID>
