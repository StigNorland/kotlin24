package no.nsd.qddt.repository
import no.nsd.qddt.model.ChangeFeed
import no.nsd.qddt.model.ChangeFeedKey
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author Stig Norland
 */
@Repository
interface ChangeFeedRepository : JpaRepository<ChangeFeed?, ChangeFeedKey?> {
    fun findByNameLikeIgnoreCaseOrRefChangeKindLikeIgnoreCaseOrRefKindLikeIgnoreCase(
        name: String?,
        changeKind: String?,
        kind: String?,
        pageable: Pageable?
    ): Page<ChangeFeed?>?

    @Query(
        value = "SELECT cl FROM ChangeFeed cl " +
                "WHERE  lower(cl.name) like :name or lower(cl.refChangeKind) LIKE :changeKind or lower(cl.refKind) LIKE :kind "
                + "ORDER BY ?#{#pageable}", countQuery = "SELECT count(cl) FROM ChangeFeed cl " +
                "WHERE  lower(cl.name) like :name or lower(cl.refChangeKind) LIKE :changeKind or lower(cl.refKind) LIKE :kind "
                + "ORDER BY ?#{#pageable}", nativeQuery = false
    )
    fun findByQuery(
        @Param("name") name: String?,
        @Param("changeKind") changeKind: String?,
        @Param("kind") kind: String?, pageable: Pageable?
    ): Page<ChangeFeed?>?
}