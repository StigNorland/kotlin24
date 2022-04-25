package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Study
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "studyListe", types = [Study::class])
interface StudyListe : IAbstractEntityViewList {
    var label: String
    var isArchived: Boolean

    @Value(value = "#{target.modified.getTime() }")
    override fun getModified(): Long

    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String


}
