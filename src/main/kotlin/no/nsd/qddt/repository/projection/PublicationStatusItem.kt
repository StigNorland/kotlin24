package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.PublicationStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "publicationStatusItem", types = [PublicationStatus::class])
interface PublicationStatusItem {
    var id: Int
    var label: String
    var description: String
    @Value(value = "#{target.published.toString() }")
    fun getPublished(): String?

}