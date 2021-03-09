package no.nsd.qddt.repository

import no.nsd.qddt.model.Publication
import no.nsd.qddt.repository.projection.PublicationListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "publication", itemResourceRel = "Publication", excerptProjection = PublicationListe::class)
interface PublicationRepository: BaseArchivedRepository<Publication> {

    @Query(nativeQuery = true,
        value = "SELECT c.* FROM study c " +
                "WHERE ( c.change_kind !='BASED_ON' and (" +
                " (:name is null OR c.name ILIKE cast(:name AS text)) " +
                " OR  (:description is null OR c.description ILIKE cast(:description AS text)) " +
                ")) ",
        countQuery = "SELECT count(c.*) FROM study c " +
                "WHERE ( c.change_kind !='BASED_ON' and (" +
                " (:name is null OR c.name ILIKE cast(:name AS text)) " +
                " OR  (:description is null OR c.description ILIKE cast(:description AS text)) " +
                ")) ",
    )
    fun findByQuery(@Param("name") name:String?, @Param("description") description:String?, pageable: Pageable): Page<Publication>

}
