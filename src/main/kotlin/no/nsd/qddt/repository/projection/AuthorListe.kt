package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Author
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "authorListe", types = [Author::class])
interface AuthorListe {
    val id: UUID
    var name: String
    var email: String?
    var about: String?
    var homepageUrl: String?
    var pictureUrl: String?
    var authorsAffiliation: String?

    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long
}
