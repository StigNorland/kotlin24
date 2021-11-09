package no.nsd.qddt.repository

import no.nsd.qddt.model.Publication
import no.nsd.qddt.model.User
import no.nsd.qddt.repository.criteria.PublicationCriteria
import no.nsd.qddt.repository.projection.PublicationListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.ModelAttribute
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
                "WHERE ( ps.published = :criteria.publishedKind " +
                "AND ( p.agency_id = CAST(:criteria.principal.agencyId AS uuid)  or 'EXTERNAL_PUBLICATION' = :criteria.publishedKind) " +
                "AND (:criteria.publicationStatus is null or ps.label similar to :criteria.publicationStatus) " +
                "OR (:criteria.name is null or p.name ILIKE  searchStr(:criteria.name) ) " +
                "OR (:criteria.purpose is null or p.purpose ILIKE searchStr(:criteria.purpose) ) )",
        countQuery = "SELECT count(p.*) FROM publication p " +
                "LEFT JOIN publication_status ps ON p.status_id = ps.id " +
                "WHERE ( ps.published = :publishedKind " +
                "AND ( p.agency_id = CAST(:criteria.principal.agencyId AS uuid)  or 'EXTERNAL_PUBLICATION' = :criteria.publishedKind) " +
                "AND (:criteria.publicationStatus is null or ps.label similar to :criteria.publicationStatus) " +
                "OR (:criteria.name is null or p.name ILIKE  searchStr(:criteria.name) ) " +
                "OR (:criteria.purpose is null or p.purpose ILIKE searchStr(:criteria.purpose) ) )",
    )
    fun findByQuery(
        @ModelAttribute("criteria") criteria: PublicationCriteria,
        @Param("pageable") pageable: Pageable?
    ): Page<Publication>

}
