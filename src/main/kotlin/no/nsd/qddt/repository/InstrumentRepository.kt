package no.nsd.qddt.repository

import no.nsd.qddt.model.Instrument
import no.nsd.qddt.repository.projection.InstrumentListe
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

@RepositoryRestResource(path = "instrument", itemResourceRel = "Instrument", excerptProjection = InstrumentListe::class)
interface InstrumentRepository: BaseArchivedRepository<Instrument> {

    @Query(nativeQuery = true,
        value = "SELECT c.* FROM instrument c " +
                "WHERE ( c.change_kind !='BASED_ON' " +
                "AND (c.name ILIKE searchStr(cast(:name AS text)) " +
                "OR c.label ILIKE searchStr(cast(:label AS text)) " +
                "OR c.description ILIKE searchStr(cast(:description AS text)) )) ",
        countQuery = "SELECT count(c.*) FROM instrument c " +
                "WHERE ( c.change_kind !='BASED_ON' " +
                "AND (c.name ILIKE searchStr(cast(:name AS text)) " +
                "OR c.label ILIKE searchStr(cast(:label AS text)) " +
                "OR c.description ILIKE searchStr(cast(:description AS text)) )) ",
    )
    fun findByQuery(
        @Param("label") label:String?,
        @Param("name") name:String?,
        @Param("description") description:String?,
        pageable: Pageable?): Page<Instrument>?

}
