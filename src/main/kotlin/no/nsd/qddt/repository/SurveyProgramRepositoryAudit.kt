package no.nsd.qddt.repository

import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.repository.projection.SurveyProgramListe
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "surveyprograms", collectionResourceRel = "surveyprogram", itemResourceRel = "SurveyProgram", excerptProjection = SurveyProgramListe::class)
interface SurveyProgramRepositoryAudit  : RevisionRepository<SurveyProgram, UUID, Int>
