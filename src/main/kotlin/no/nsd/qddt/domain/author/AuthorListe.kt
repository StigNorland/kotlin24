package no.nsd.qddt.domain.author

import org.springframework.data.rest.core.config.Projection
import java.net.URL
import java.sql.Timestamp
import java.util.*

@Projection(name = "authorListe", types = [Author::class])
interface AuthorListe {
    val id: UUID
    var updated : Timestamp
    var name: String
    var email: String?
    var homepage: URL?
}
