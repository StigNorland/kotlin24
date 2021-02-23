package no.nsd.qddt.security

/**
 * @author Stig Norland
 */
import no.nsd.qddt.config.SecurityConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse




@Component
class AuthTokenFilter : OncePerRequestFilter() {

    @Autowired
    private lateinit var userDetailsService: AuthUserDetailsService

    @Autowired
    private lateinit var jwtUtils: AuthTokenUtil

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest,response: HttpServletResponse,filterChain: FilterChain) {
        try {
            val header = request.getHeader(HttpHeaders.AUTHORIZATION)
            if (header.isNullOrBlank() || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response)
                return
            }
            val token = header.split(" ")[1].trim()
            if (!jwtUtils.validateJwtToken(token)) {
                filterChain.doFilter(request, response)
                return
            }

            // Get user identity and set it on the spring security context
            val userDetails = userDetailsService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token))

            val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails?.authorities).run {
                details = WebAuthenticationDetailsSource().buildDetails(request)
                this
            }

            SecurityContextHolder.getContext().authentication = authentication


//            userDetailsService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token))?.let { user ->
//                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(user, null, user.authorities).let{
//                        it.details = WebAuthenticationDetailsSource().buildDetails(request)
//                        it
//                    }
//            }

        } catch (e: Exception) {
            logger.error("Cannot set user authentication: {}", e)
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(SecurityConfig::class.java)
    }

}
