package no.nsd.qddt.repository

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.interfaces.BaseArchivedRepository
import no.nsd.qddt.repository.projection.StudyListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
* @author Stig Norland
*/
@RepositoryRestResource(path = "study", collectionResourceRel = "Studies", itemResourceRel = "Study", excerptProjection = StudyListe::class)
interface StudyRepository:BaseArchivedRepository<Study> , RevisionRepository<Study, UUID, Int>,
    JpaRepository<Study, UUID> {

    @Query(value = ("SELECT c.* FROM study c " +
                  "WHERE ( c.change_kind !='BASED_ON' and (c.name ILIKE :name or c.description ILIKE :description) ) "
                  + "ORDER BY ?#{#pageable}"),
        countQuery = ("SELECT count(c.*) FROM study c " +
                    "WHERE ( c.change_kind !='BASED_ON' and (c.name ILIKE :name or c.description ILIKE :description) ) "
                    + "ORDER BY ?#{#pageable}"), nativeQuery = true)
    fun findByQuery(@Param("name") name:String, @Param("description") description:String, pageable:Pageable):Page<Study>

}
