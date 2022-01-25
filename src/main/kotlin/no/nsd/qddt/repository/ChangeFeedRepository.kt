package no.nsd.qddt.repository
import no.nsd.qddt.model.ChangeFeed
import no.nsd.qddt.model.ChangeFeedKey
import no.nsd.qddt.repository.projection.CommentListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.stereotype.Repository

/**
 * @author Stig Norland
 */
//@RepositoryRestResource(path = "changelog",  itemResourceRel = "ChangeFeed") //, excerptProjection = CommentListe::class)
@Repository
interface ChangeFeedRepository : JpaRepository<ChangeFeed, ChangeFeedKey> {

//    @RestResource(rel = "findByQuery", path = "findByQuery")
    fun findByNameLikeIgnoreCaseAndRefChangeKindLikeIgnoreCaseAndRefKindLikeIgnoreCase(
        name: String?="%",
        changeKind: String?="%",
        kind: String?="%",
        pageable: Pageable?
    ): Page<ChangeFeed>

//    @Query(nativeQuery = false,
//        value = "SELECT cl FROM ChangeFeed cl " +
//                "WHERE  lower(cl.name) like :name or lower(cl.refChangeKind) LIKE :changeKind or lower(cl.refKind) LIKE :kind "
//        ,
//        countQuery = "SELECT count(cl) FROM ChangeFeed cl " +
//                "WHERE  lower(cl.name) like :name or lower(cl.refChangeKind) LIKE :changeKind or lower(cl.refKind) LIKE :kind "
//    )
//    fun findByQuery(
//        @Param("name") name: String?,
//        @Param("changeKind") changeKind: String?,
//        @Param("kind") kind: String?, pageable: Pageable?
//    ): Page<ChangeFeed?>?
}