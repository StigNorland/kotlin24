package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.OtherMaterial
import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.model.embedded.ElementRefQuestionItem
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

    @Value(value = "#{target.questionItems }")
    fun getQuestionItems(): MutableList<ElementRefQuestionItem>

    @Value(value = "#{target.otherMaterials }")
    fun getOtherMaterials(): MutableList<OtherMaterial>

    @Value(value = "#{target.comments }")
    fun getComments(): List<CommentListe>


}

