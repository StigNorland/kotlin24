package no.nsd.qddt.domain.classes.interfaces

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable
import java.util.*

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 *
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
@NoRepositoryBean
interface BaseRepository<T, ID : Serializable?> : JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    override fun findById(id: ID): Optional<T>
}
