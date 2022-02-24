package no.nsd.qddt.repository
import no.nsd.qddt.model.views.ChangeFeed
import no.nsd.qddt.model.views.ChangeFeedKey
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
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


}