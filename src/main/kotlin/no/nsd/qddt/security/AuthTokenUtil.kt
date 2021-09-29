package no.nsd.qddt.security

import io.jsonwebtoken.*
import no.nsd.qddt.model.User
import no.nsd.qddt.utils.StringTool
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*

/**
 * @author Stig Norland
 */


@Component
class AuthTokenUtil: Serializable {

    @Value("\${security.jwt.token.secret}")
    private val  jwtSecret: String? = null
    @Value("\${security.jwt.token.expiration-time}")
    private val  jwtExpirationMs: Long? = null


    protected class AgencyJ(val id:String, val name: String, val xmlLang: String):Serializable

    fun generateJwtToken(authentication: Authentication): String {
        val userDetails = authentication.principal as User
        val expira = jwtExpirationMs?:  (1000 * 60 * 60 * 24L)

        val claims = Jwts.claims()
            .setId(UUID.randomUUID().toString())
            .setSubject(StringTool.CapString(userDetails.username))
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + expira))

        claims["role"] = userDetails.authorities.joinToString { it.authority }
        claims["modified"] = userDetails.modified.toLocalDateTime().toString()
        claims["id"] = userDetails.id.toString()
        claims["email"] = userDetails.email

        return AuthResponse(Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS256, jwtSecret).compact()).toString()
    }

//    fun getUserNameFromJwtToken(token: String?): String {
//        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body.subject
//    }

    fun getEmailFromJwtToken(token: String?): String {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body.getValue("email") as String
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
        private val logger = LoggerFactory.getLogger(AuthTokenUtil::class.java)
    }
}
