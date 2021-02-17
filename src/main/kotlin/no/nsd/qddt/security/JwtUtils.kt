package no.nsd.qddt.security

import io.jsonwebtoken.*
import no.nsd.qddt.model.User
import no.nsd.qddt.utils.StringTool
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*

/**
 * @author Stig Norland
 */


@Component
class JwtUtils {
    @Value("\${qddt.security.secret}")
    private lateinit var jwtSecret: String

    @Value("\${qddt.security.expiration-time}")
    private lateinit var jwtExpirationMs: Optional<Long>


    fun generateJwtToken(authentication: Authentication): String {
        val userDetails = authentication.principal as User
        val claims = Jwts.claims()
            .setId(UUID.randomUUID().toString())
            .setSubject(StringTool.CapString(userDetails.username))
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs.get()))

        claims["role"] = userDetails.authorities
        claims["modified"] = userDetails.modified?.time
        claims["id"] = userDetails.id.toString()
        claims["email"] = userDetails.email
        claims["agency"] = userDetails.agency.let {  it.name }

        return  AuthResponse(Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact()).toString()
    }

    fun getUserNameFromJwtToken(token: String?): String {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body.subject
    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken)
            return true
        } catch (e: SignatureException) {
            logger.error("Invalid JWT signature: {}", e.message)
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", e.message)
        }
        return false
    }


    companion object {
        private val logger = LoggerFactory.getLogger(JwtUtils::class.java)
    }
}
