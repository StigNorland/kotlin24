package no.nsd.qddt.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

/**
 * @author Stig Norland
 */
@Component
class JwtTokenUtil  {

    @Value("\${jwt-security.secret}")
    private lateinit var  secret: String

    private val JWT_TOKEN_VALIDITY = (5 * 60 * 60).toLong()

    fun getUsernameFromToken(token: String): String {
        return getClaimFromToken(token) { it.subject }
    }

    fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken(token) { it.expiration }
    }

    fun <T> getClaimFromToken(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .body
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(user: UserDetails): String {
        return doGenerateToken(user.username)
    }

    private fun doGenerateToken(subject: String): String {

        val claims = Jwts.claims().setSubject(subject)
        claims["scopes"] = Arrays.asList(SimpleGrantedAuthority("ROLE_ADMIN"))

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()

//        return Jwts.builder()
//            .setClaims(claims)
//            .setIssuer("http://devglan.com")
//            .setIssuedAt(Date(System.currentTimeMillis()))
//            .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
//            .signWith(SignatureAlgorithm.HS256, secret)
//            .compact()
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }




//    fun generateToken(userDetails: UserDetails): String {
//        val claims: Map<String, Any> = HashMap()
//        return doGenerateToken(claims, userDetails.username)
//    }
//
//    private fun doGenerateToken(claims: Map<String, Any>, subject: String): String {
//        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(Date(System.currentTimeMillis()))
//            .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
//            .signWith(SignatureAlgorithm.HS512, secret).compact()
//    }
//
//    fun canTokenBeRefreshed(token: String): Boolean {
//        return !isTokenExpired(token) || ignoreTokenExpiration(token)
//    }
//
//    fun validateToken(token: String, userDetails: UserDetails): Boolean {
//        val username = getUsernameFromToken(token)
//        return username == userDetails.getUsername() && !isTokenExpired(token)
//    }

}
