package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Concept
import no.nsd.qddt.model.embedded.ElementRefQuestionItem
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "conceptListe", types = [Concept::class])
interface ConceptListe: IAbstractEntityViewList {
    var label: String
    var description: String
    var isArchived: Boolean

    @Value(value = "#{target.modified.getTime() }")
    override fun getModified(): Long

    @Value(value = "#{target.questionItems }")
    fun getQuestionItems(): MutableList<ElementRefQuestionItem>

    @Value(value = "#{target.children }")
    fun getChildren(): List<ConceptListe>

    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String

}
