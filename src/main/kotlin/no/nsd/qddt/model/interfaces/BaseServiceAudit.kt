package no.nsd.qddt.model.interfaces

import org.springframework.dao.DataAccessException
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision

/**
 * Interface for all service classes dealing with entity classes
 * annotated by [org.hibernate.envers.Audited]
 *
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */
interface BaseServiceAudit<T, ID, N> where N : Number?, N : Comparable<N>? {

    /**
     * Store object T to backstore
     * @param instance object T
     * @return saved instance T (may have fields updated by backstore)
     */
    //    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR')")
    fun <S : T?> save(instance: S): S

    /**
     * Deletes object with id ID from backstore, exception raised by failure.
     * @param id ID of entity
     */
    //    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Throws(DataAccessException::class)
    fun delete(id: ID)


    /**
     * Returns a [Page] of entities matching the given [Example]. In case no match could be found, an empty
     * [Page] is returned.
     *
     * @param example must not be null.
     * @param pageable can be null.
     * @return a [Page] of entities matching the given [Example].
     */
    fun <S : T?> findAll(example: Example<S>?, pageable: Pageable?): Page<S>?

    /**
     * Find the latest changed revision.
     * @param id of the entity
     * @return [Revision]
     */
    fun findLastChange(id: ID): Revision<N, T>?

    /**
     * Find the entity based on a revision number.
     * @param id of the entity
     * @param revision number of the entity
     * @return [Revision] at the given revision
     */
    fun findRevision(id: ID, revision: N): Revision<N, T>?

    /**
     * Find all revisions and return in a pageable view
     * @param id of the entity
     * @param pageable from controller method
     * @return [Page] of the entity
     */
    fun findRevisions(id: ID, pageable: Pageable?): Page<Revision<N, T>?>?

    /**
     * Find the latest changed revision.
     * @param id of the entity
     * @return [Revision]
     */
    fun findFirstChange(id: ID): Revision<N, T>?

//    @NotNull
    fun setShowPrivateComment(showPrivate: Boolean)
}
