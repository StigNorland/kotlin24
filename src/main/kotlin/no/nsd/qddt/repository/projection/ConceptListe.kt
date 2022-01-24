package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Concept
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.embedded.ElementRefEmbedded
import no.nsd.qddt.model.embedded.Version
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "conceptListe", types = [Concept::class])
interface ConceptListe: IAbstractEntityViewList {
    var label: String
    var description: String
    var isArchived: Boolean

    @Value(value = "#{target.modified.getTime() }")
    override fun getModified(): Long


    @Value(value = "#{target.questionItems }")
    fun getQuestionItems(): MutableList<ElementRefEmbedded<QuestionItem>>

    @Value(value = "#{target.children }")
    fun getChildren(): List<ConceptListe>

}
