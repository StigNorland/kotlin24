package no.nsd.qddt.domain.classes.interfaces

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
