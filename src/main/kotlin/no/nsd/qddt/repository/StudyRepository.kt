package no.nsd.qddt.repository

import no.nsd.qddt.model.Study
import no.nsd.qddt.repository.projection.StudyListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.*

/**
* @author Stig Norland
*/
@RepositoryRestResource(path = "study",  itemResourceRel = "Study", excerptProjection = StudyListe::class)
interface StudyRepository: BaseArchivedRepository<Study> {


    @RestResource(rel = "revision", path = "rev")
    override fun findRevisions(id: UUID, pageable: Pageable): Page<Revision<Int, Study>>

    @RestResource(rel = "all", path = "list")
    override fun findAll(pageable: Pageable): Page<Study>


    @Query(nativeQuery = true,
        value =
            "SELECT c.* FROM concept_hierarchy c " +
            "WHERE ( c.change_kind !='BASED_ON' and class_kind='STUDY' and (c.name ILIKE searchStr(cast(:name AS text))  or c.description ILIKE searchStr(cast(:description AS text)) ) ) ",
        countQuery =
            "SELECT count(c.*) FROM concept_hierarchy c " +
            "WHERE ( c.change_kind !='BASED_ON' and class_kind='STUDY' and (c.name ILIKE searchStr(cast(:name AS text))  or c.description ILIKE searchStr(cast(:description AS text)) ) ) "
    )
    fun findByQuery(@Param("name") name:String, @Param("description") description:String, pageable:Pageable):Page<Study>

}
