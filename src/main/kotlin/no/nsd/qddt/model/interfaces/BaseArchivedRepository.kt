package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.classes.AbstractEntityAudit
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.query.Param

/**
 * @author Stig Norland
 */
@NoRepositoryBean
interface BaseArchivedRepository<T: AbstractEntityAudit>  {
    //    @Query(value = "select count(*) from project_archived_hierarchy as pah  where is_archived and  pah.ancestors  = ANY(:idUser) "
    @Query(
        name= "hasArchive", nativeQuery = true,
        value = "select count(*) from project_archived_hierarchy as pah  where is_archived and  pah.ancestors  @> ARRAY[CAST(:entityId AS uuid)];",
    )
    fun hasArchive(@Param("entityId") entityId: String): Long

}
