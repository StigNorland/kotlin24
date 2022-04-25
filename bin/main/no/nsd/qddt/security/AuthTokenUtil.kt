package no.nsd.qddt.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import no.nsd.qddt.model.User
import no.nsd.qddt.utils.StringTool
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.io.Serializable
import java.security.Key
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

    fun generateJwtToken(authentication: Authentication): String {
        val userDetails = authentication.principal as User
        val expira = jwtExpirationMs?:  (1000 * 60 * 60 * 24L)
        val key: Key = Keys.hmacShaKeyFor(jwtSecret!!.encodeToByteArray())

        val claims = Jwts.claims()
            .setId(UUID.randomUUID().toString())
            .setSubject(StringTool.capString(userDetails.username))
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + expira))

        claims["role"] = userDetails.authorities.joinToString { it.authority }
        claims["modified"] = userDetails.modified!!.toLocalDateTime().toString()
        claims["id"] = userDetails.id.toString()
        claims["email"] = userDetails.email




        return AuthResponse(Jwts.builder()
            .setClaims(claims)
            .signWith(key)
            .compact()).toString()
    }

    fun getEmailFromJwtToken(token: String?): String {
        val key: Key = Keys.hmacShaKeyFor(jwtSecret!!.encodeToByteArray())

        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build().parseClaimsJws(token).body.getValue("email") as String
    }

    fun validateJwtToken(authToken: String?): Boolean {
        val key: Key = Keys.hmacShaKeyFor(jwtSecret!!.encodeToByteArray())

        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(authToken)
            return true
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
