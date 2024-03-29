package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

/**
 * @author Stig Norland
 */
@Projection(name = "userListe", types = [User::class])
interface UserListe {
    val id: UUID
    val username: String
    var email : String

    @Value(value = "#{target.username  + '@' + target.agency?.name?:'xxx' }")
    fun getUserAgencyName(): String

}

