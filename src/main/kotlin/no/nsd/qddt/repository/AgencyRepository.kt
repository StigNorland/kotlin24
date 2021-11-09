package no.nsd.qddt.repository

import no.nsd.qddt.model.Agency
import no.nsd.qddt.model.Author
import no.nsd.qddt.repository.projection.AgencyListe
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */

@RepositoryRestResource(path = "agency", itemResourceRel = "Agency", excerptProjection = AgencyListe::class)
interface AgencyRepository : JpaRepository<Agency,UUID> {

    @Cacheable(cacheNames = ["AGENCIES"])
    override fun findById(id: UUID): Optional<Agency>

    @Cacheable(cacheNames = ["AGENCIES"])
    override fun findAll(): MutableList<Agency>

    @Cacheable(cacheNames = ["AGENCIES"])
    override fun findAll(pageable: Pageable): Page<Agency>

    @Query(
        value = "SELECT ca.* FROM agency ca WHERE " +
                "( ca.name  ILIKE searchStr(:name)) " ,
        countQuery = "SELECT count(ca.*) FROM agency ca WHERE " +
                "( ca.name  ILIKE searchStr(:name) )" ,
        nativeQuery = true
    )
    fun findByQuery(
        @Param("name") name: String?,
        pageable: Pageable?
    ): Page<Agency?>?
}
