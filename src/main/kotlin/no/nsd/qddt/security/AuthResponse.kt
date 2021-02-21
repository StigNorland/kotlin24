package no.nsd.qddt.security

import java.io.Serializable

/**
 * @author Stig Norland
 */
class AuthResponse(private val token: String) : Serializable {

    override fun toString(): String {
        return String.format("{\"token\":\"%s\"}", token)
    }

}
