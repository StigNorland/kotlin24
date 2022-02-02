package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.TopicGroup
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

/**
 * @author Stig Norland
 */
//@JsonPropertyOrder("id", "label", "name", "isArchived")
@Projection(name = "topicGroupListe", types = [TopicGroup::class])
interface TopicGroupListe : IAbstractEntityViewList {

    var label: String
    var description: String


    var isArchived: Boolean

    @Value(value = "#{target.modified.getTime() }")
    override fun getModified(): Long

    @Value(value = "#{target.modifiedBy.username  + '@' + target.modifiedBy.agency?.name }")
    fun getModifiedBy(): String?

    @Value(value = "#{target.comments }")
    fun getComments(): List<CommentListe>
//    var children: List<ConceptListe>

}

