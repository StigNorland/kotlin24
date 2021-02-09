package no.nsd.qddt.classes.interfaces

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.query.Param
import java.io.Serializable

/**
 * @author Stig Norland
 */
@NoRepositoryBean
interface BaseArchivedRepository<T, ID : Serializable?> : BaseRepository<T, ID> {
    //    @Query(value = "select count(*) from project_archived_hierarchy as pah  where is_archived and  pah.ancestors  = ANY(:idUser) "
    @Query(
        value = "select count(*) from project_archived_hierarchy as pah  where is_archived and  pah.ancestors  @> ARRAY[CAST(:entityId AS uuid)];",
        nativeQuery = true
    )
    fun hasArchive(@Param("entityId") entityId: String?): Long
}
