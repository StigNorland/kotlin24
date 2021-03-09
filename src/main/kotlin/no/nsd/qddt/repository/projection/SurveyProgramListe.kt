package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.SurveyProgram
import org.springframework.data.rest.core.config.Projection

@Projection(name = "surveyProgramListe", types = [SurveyProgram::class])
interface SurveyProgramListe: IAbstractEntityViewList {
    var description: String
    // var studies: List<Study>
    var isArchived: Boolean
}

