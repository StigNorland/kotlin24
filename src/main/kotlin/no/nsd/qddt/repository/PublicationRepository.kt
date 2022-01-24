package no.nsd.qddt.repository

import no.nsd.qddt.model.Publication
import no.nsd.qddt.repository.projection.PublicationListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*


/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "publication", itemResourceRel = "Publication", excerptProjection = PublicationListe::class)
interface PublicationRepository: BaseArchivedRepository<Publication> {

//    @RestResource(rel = "findByAgencyId", path = "byAgency")
//    fun findAllByAgencyId(agnecyId: UUID, pageable: Pageable?): Page<Publication>

    @Query( nativeQuery = true,
        value = "SELECT p.*  FROM publication p " +
                "LEFT JOIN publication_status ps ON p.status_id = ps.id " +
                "WHERE ps.published = cast(:publishedKind as text) AND xml_lang ILIKE :xmlLang " +
                "AND ( p.agency_id = :agencyId  or 'EXTERNAL_PUBLICATION' = cast(:publishedKind as text)) " +
                "AND ( ps.label similar to cast(:publicationStatus AS text) " +
                "OR p.name ILIKE  searchStr(cast(:name as text))  " +
                "OR p.purpose ILIKE searchStr(cast(:purpose as text)) )",
        countQuery = "SELECT count(p.*) FROM publication p " +
                "LEFT JOIN publication_status ps ON p.status_id = ps.id " +
                "WHERE ps.published = cast(:publishedKind as text) AND xml_lang ILIKE :xmlLang " +
                "AND ( p.agency_id = :agencyId  or 'EXTERNAL_PUBLICATION' = cast(:publishedKind as text)) " +
                "AND ( ps.label similar to cast(:publicationStatus AS text) " +
                "OR p.name ILIKE  searchStr(cast(:name as text))  " +
                "OR p.purpose ILIKE searchStr(cast(:purpose as text)) )"
    )
    fun findByQuery(
        @Param("publishedKind") publishedKind:String,
        @Param("publicationStatus") publicationStatus:String,
        @Param("purpose") purpose:String,
        @Param("xmlLang") xmlLang:String,
        @Param("name") name:String,
        @Param("agencyId") agencyId:UUID,
        pageable: Pageable?
    ): Page<Publication>
}
