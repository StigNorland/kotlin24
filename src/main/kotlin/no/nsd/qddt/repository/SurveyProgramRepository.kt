package no.nsd.qddt.repository

import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.model.interfaces.BaseArchivedRepository
import no.nsd.qddt.repository.projection.SurveyProgramListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
* @author Stig Norland
*/
@RepositoryRestResource(path = "surveyprograms", collectionResourceRel = "surveyprogram", itemResourceRel = "SurveyProgram", excerptProjection = SurveyProgramListe::class)
interface SurveyProgramRepository:BaseArchivedRepository<SurveyProgram, UUID> {

    @Query(value = ("SELECT c.* FROM study c " +
                  "WHERE ( c.change_kind !='BASED_ON' and (c.name ILIKE :name or c.description ILIKE :description) ) "
                  + "ORDER BY ?#{#pageable}"),
        countQuery = ("SELECT count(c.*) FROM study c " +
                    "WHERE ( c.change_kind !='BASED_ON' and (c.name ILIKE :name or c.description ILIKE :description) ) "
                    + "ORDER BY ?#{#pageable}"), nativeQuery = true)
    fun findByQuery(@Param("name") name:String, @Param("description") description:String, pageable:Pageable):Page<SurveyProgram>

}
