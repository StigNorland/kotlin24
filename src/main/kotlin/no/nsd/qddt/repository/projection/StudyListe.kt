package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.embedded.Version
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "studyListe", types = [Study::class])
interface StudyListe : IAbstractEntityViewList {
    override var id: UUID
    var label: String
    override var name: String
    override var version: Version
    override var xmlLang: String
    override var classKind: String

    @Value(value = "#{target.modified.getTime() }")
    override fun getModified(): Long

//    @Value(value = "#{target.modifiedBy.username  + '@' + target.modifiedBy.agency?.name }")
//    override fun getModifiedBy(): String?


//    var children: List<TopicGroupListe>
    var isArchived: Boolean
}
