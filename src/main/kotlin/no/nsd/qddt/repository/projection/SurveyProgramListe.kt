package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.SurveyProgram
import no.nsd.qddt.model.embedded.Version
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "surveyProgramListe", types = [SurveyProgram::class])
interface SurveyProgramListe {
    var id: UUID
    var name: String
    var label: String
    var description: String

    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long

    @Value(value = "#{target.modifiedBy.username  + '@' + target.modifiedBy.agency?.name }")
    fun getModifiedBy(): String?

    var version: Version

//    @Value(value = "#{target.children }")
//    fun getChildren(): Set<StudyListe>
    var isArchived: Boolean
}

