package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.PublicationStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "publicationStatusListe", types = [PublicationStatus::class])
interface PublicationStatusListe {
    var id: Int
    var label: String
    var description: String
    @Value(value = "#{target.published.toString() }")
    fun getPublished(): String?

    @Value(value = "#{target.children }")
    fun getChildren(): List<PublicationStatusListe>
}


