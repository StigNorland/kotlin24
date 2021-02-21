package no.nsd.qddt.model.interfaces

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
interface BaseRepository<T : AbstractEntityAudit> :
    RevisionRepository<T, UUID, Int>,
    JpaRepository<T, UUID>
//    JpaSpecificationExecutor<T>
