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
//    override fun findLastChangeRevision(id: UUID): Optional<Revision<Int, T>>
//
//    override fun findRevisions(id: UUID): Revisions<Int, T>
//
//    override fun findRevisions(id: UUID, pageable: Pageable): Page<Revision<Int, T>>
//
//    override fun findRevision(id: UUID, revisionNumber: Int): Optional<Revision<Int, T>>
//
//    override fun <S : T> save(entity: S): S
//
//    override fun findById(id: UUID): Optional<T>
//
//    override fun findAll(): MutableList<T>
//
//    override fun findAll(pageable: Pageable): Page<T>
//
//    override fun <S : T> findAll(example: Example<S>, pageable: Pageable): Page<S>
//
//    override fun deleteById(id: UUID)
//
//    override fun <S : T> findOne(example: Example<S>): Optional<S>
}
//    JpaSpecificationExecutor<T>
