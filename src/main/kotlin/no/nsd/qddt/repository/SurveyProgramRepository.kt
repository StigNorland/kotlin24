package no.nsd.qddt.repository

import no.nsd.qddt.model.Agency
import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.repository.projection.SurveyProgramListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.data.history.Revisions
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
@RepositoryRestResource(path = "surveyprogram",  itemResourceRel = "SurveyProgram", excerptProjection = SurveyProgramListe::class )
interface SurveyProgramRepository: BaseArchivedRepository<SurveyProgram> {

    // @RestResource(rel = "revision", path = "rev")
    override fun findRevisions(id: UUID, pageable: Pageable): Page<Revision<Int, SurveyProgram>>

    override fun findRevisions(id: UUID): Revisions<Int, SurveyProgram> {
        TODO("Not yet implemented")
    }

    override fun findAll(pageable: Pageable ): Page<SurveyProgram>


    @Query(nativeQuery = true,
        value = "SELECT c.* FROM concept_hierarchy c " +
                  "WHERE ( c.change_kind !='BASED_ON' and class_kind='SURVEY_PROGRAM' and (c.name ILIKE :name or c.description ILIKE :description) ) ",
        countQuery ="SELECT count(c.*) FROM concept_hierarchy c " +
                    "WHERE ( c.change_kind !='BASED_ON' and class_kind='SURVEY_PROGRAM' and (c.name ILIKE :name or c.description ILIKE :description) ) ",
    )
    fun findByQuery(@Param("name") name:String?, @Param("description") description:String?, pageable:Pageable):Page<SurveyProgram>

    fun findByAgency(agency: Agency): List<SurveyProgram>

}
