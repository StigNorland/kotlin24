package no.nsd.qddt.repository

import no.nsd.qddt.model.Authority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "authority",  itemResourceRel = "Authority")
interface AuthorityRepository : JpaRepository<Authority,UUID>
