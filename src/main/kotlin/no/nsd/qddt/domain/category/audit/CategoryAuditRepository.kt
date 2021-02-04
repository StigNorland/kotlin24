package no.nsd.qddt.domain.category.audit

import no.nsd.qddt.domain.AbstractEntityAudit.ChangeKind
import no.nsd.qddt.domain.category.Category
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author Dag Østgulen Heradstveit
 */
@Repository
internal interface CategoryAuditRepository : RevisionRepository<Category?, UUID?, Int?> {
    //    @Query(value= "select distinct * from concept_aud " +
    //            "where id = :id and major = :major and minor = :minor " +
    //            "order by rev asc limit 1", nativeQuery = true)
    //    Revision<Integer,Category> findVersion(@Param("id") UUID id, @Param("major") int major,@Param("minor") int minor);
    // @JsonView(View.Audit.class)
    fun findRevisionsByIdAndChangeKindNotIn(
        uuid: UUID?,
        changeKinds: Collection<ChangeKind?>?,
        pageable: Pageable?
    ): Page<Revision<Int?, Category?>?>?
}
