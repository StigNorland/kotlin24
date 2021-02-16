package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Author
import org.springframework.data.rest.core.config.Projection
import java.net.URL
import java.sql.Timestamp
import java.util.*

@Projection(name = "authorListe", types = [Author::class])
interface AuthorListe {
    val id: UUID
    var name: String
    var modified : Timestamp
    var email: String?
    var homepage: URL?
}
