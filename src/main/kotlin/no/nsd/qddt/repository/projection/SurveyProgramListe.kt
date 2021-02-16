package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import org.springframework.data.rest.core.config.Projection

@Projection(name = "surveyProgramListe", types = [SurveyProgram::class])
interface SurveyProgramListe: IWebMenuPreview

