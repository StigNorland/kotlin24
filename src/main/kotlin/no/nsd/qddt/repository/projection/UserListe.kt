package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.sql.Timestamp
import java.util.*

/**
 * @author Stig Norland
 */
@Projection(name = "userListe", types = [User::class])
interface UserListe {
    val id: UUID
    @Value(value = "#{target.username }")
    fun getName(): String
    var email : String
    var agencyId: UUID?
}

