package no.nsd.qddt.repository

import no.nsd.qddt.model.Publication
import no.nsd.qddt.model.User
import no.nsd.qddt.repository.projection.PublicationListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.core.annotation.AuthenticationPrincipal
import java.security.Principal
import java.util.UUID


/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "publication", itemResourceRel = "Publication", excerptProjection = PublicationListe::class)
interface PublicationRepository: BaseArchivedRepository<Publication> {

    @RestResource(rel = "findByAgencyId", path = "byAgency")
    fun findAllByAgencyId(agnecyId: UUID, pageable: Pageable?): Page<Publication>

    @Query( nativeQuery = true,
        value = "SELECT p.*  FROM publication p " +
                "LEFT JOIN publication_status ps ON p.status_id = ps.id " +
                "WHERE ( ps.published = :publishedKind " +
                "AND ( (:user is null OR p.agency_id = CAST(:principal.principal.agencyId AS uuid))  or 'EXTERNAL_PUBLICATION' = :publishedKind) " +
                "AND (:publicationStatus is null or ps.label similar to :publicationStatus) " +
                "OR (:name is null or p.name ILIKE  cast(:name AS text) ) " +
                "OR (:purpose is null or p.purpose ILIKE cast(:purpose AS text)) ) ",
        countQuery = "SELECT count(p.*) FROM publication p " +
                "LEFT JOIN publication_status ps ON p.status_id = ps.id " +
                "WHERE ( ps.published = :publishedKind " +
                "AND ( (:user is null OR p.agency_id = CAST(:principal.principal.agencyId AS uuid))  or 'EXTERNAL_PUBLICATION' = :publishedKind) " +
                "AND (:publicationStatus is null or ps.label similar to :publicationStatus) " +
                "OR (:name is null or p.name ILIKE  cast(:name AS text) ) " +
                "OR (:purpose is null or p.purpose ILIKE cast(:purpose AS text)) )"
    )
    fun findByQuery(
        @Param("name") name: String?,
        @Param("purpose") purpose: String?,
        @Param("publicationStatus") publicationStatus: String?,
        @Param("publishedKind") publishedKind: String,
//        @Param("agencyId") agencyId: UUID?,
        @Param("principal") principal: Principal,
        @Param("pageable") pageable: Pageable?
    ): Page<Publication>

}
