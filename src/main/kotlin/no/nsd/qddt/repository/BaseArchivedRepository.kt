package no.nsd.qddt.repository

import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.ElementOrder
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.query.Param

/**
 * @author Stig Norland
 */
@NoRepositoryBean
interface BaseArchivedRepository<T: AbstractEntityAudit> :BaseMixedRepository<T> {
     @Query(
        nativeQuery = true,
        value = "select count(*) from project_archived_hierarchy as pah  where is_archived and  pah.ancestors  @> ARRAY[CAST(:entityId AS uuid)];",
    )
    fun hasArchive(@Param("entityId") entityId: String): Int

    @Modifying
    @Query(
        nativeQuery = true,
        value = "update concept_hierarchy ch set parent_idx = :ranks "
    )
    fun reArrangeIndex(@Param("ranks") ranks: List<ElementOrder>)


}
