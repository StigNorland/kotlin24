package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.model.embedded.Version
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "surveyProgramListe", types = [SurveyProgram::class])
interface SurveyProgramListe {
    var id: UUID
    var label: String
    var name: String

    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long

    @Value(value = "#{target.modifiedBy.username  + '@' + target.modifiedBy.agency?.name }")
    fun getModifiedBy(): String?

    var version: Version

    var children: List<StudyListe>
    var isArchived: Boolean
}

