package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Concept
import no.nsd.qddt.model.embedded.Version
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "conceptListe", types = [Concept::class])
interface ConceptListe {

    var id: UUID

    var label: String

    var name: String

    var isArchived: Boolean

    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long

    @Value(value = "#{target.modifiedBy.username  + '@' + target.modifiedBy.agency?.name }")
    fun getModifiedBy(): String?

    var version: Version

    var children: List<ConceptListe>
}
