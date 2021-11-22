package no.nsd.qddt.repository

import no.nsd.qddt.model.QddtUrl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author Stig Norland
 */
@Repository
interface SearchRepository : JpaRepository<QddtUrl, UUID> {
    fun findByName(name: String): List<QddtUrl>
    fun findByUserId(userId: UUID): List<QddtUrl> //    @Query(value = "SELECT p.*  FROM publication p " +
    //        "LEFT JOIN publication_status ps ON p.status_id = ps.id " +
    //        "WHERE (  ps.published in :published and (p.name ILIKE :name or p.purpose ILIKE :purpose) ) "
    //        + "ORDER BY ?#{#pageable}"
    //        ,countQuery = "SELECT count(p.*) FROM publication p " +
    //        "LEFT JOIN publication_status ps ON p.status_id = ps.id " +
    //        "WHERE (  ps.published in :published and (p.name ILIKE :name or p.purpose ILIKE :purpose) ) "
    //        ,nativeQuery = true)
    //    QddtUrl findByQuery(@Param("uuid")String uuid);
}