package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.SurveyProgram
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "surveyProgramListe", types = [SurveyProgram::class])
interface SurveyProgramListe : IAbstractEntityViewList {
    var label: String

    var description: String
    var parentIdx: Long
    var isArchived: Boolean

    @Value(value = "#{target.modified.getTime() }")
    override fun getModified(): Long

    @Value(value = "#{target.modifiedBy.username  + '@' + target.modifiedBy.agency?.name }")
    fun getModifiedBy(): String?


//    @Value(value = "#{target.children }")
//    fun getChildren(): Set<StudyListe>

}

