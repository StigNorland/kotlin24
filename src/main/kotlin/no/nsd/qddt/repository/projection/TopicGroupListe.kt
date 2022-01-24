package no.nsd.qddt.repository.projection

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.model.embedded.Version
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

/**
 * @author Stig Norland
 */
//@JsonPropertyOrder("id", "label", "name", "isArchived")
@Projection(name = "topicGroupListe", types = [TopicGroup::class])
interface TopicGroupListe : IAbstractEntityViewList {

    override var id: UUID
    var label: String
    override var name: String
    override var version: Version
    override var xmlLang: String
    override var classKind: String

    var isArchived: Boolean

    @Value(value = "#{target.modified.getTime() }")
    override fun getModified(): Long

//    @Value(value = "#{target.modifiedBy.username  + '@' + target.modifiedBy.agency?.name }")
//    override fun getModifiedBy(): String?

//    var children: List<ConceptListe>

}

