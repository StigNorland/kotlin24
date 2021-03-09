package no.nsd.qddt.repository

import no.nsd.qddt.model.classes.AbstractEntityAudit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.history.RevisionRepository
import java.util.*

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 *
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
@NoRepositoryBean
interface BaseMixedRepository<T : AbstractEntityAudit> :    RevisionRepository<T, UUID, Int>,  JpaRepository<T, UUID> {
}
//    JpaSpecificationExecutor<T>
