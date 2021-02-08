package no.nsd.qddt.domain.universe

import org.springframework.data.rest.core.config.Projection
import java.sql.Timestamp
import java.util.*

/**
 * @author Stig Norland
 */
@Projection(name = "universeListe", types = [Universe::class])
interface UniverseListe {
    val id: UUID
    var username : String
    var email : String
    var updated : Timestamp
}

