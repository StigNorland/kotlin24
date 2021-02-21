package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.User
import org.springframework.data.rest.core.config.Projection
import java.sql.Timestamp
import java.util.*

/**
 * @author Stig Norland
 */
@Projection(name = "userListe", types = [User::class])
interface UserListe {
    val id: UUID
    var username : String
    var email : String
    var modified : Timestamp
}

