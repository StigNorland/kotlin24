package no.nsd.qddt.security

/**
 * @author Stig Norland
 */
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
            userDetailsService.loadUserByUsername(jwtUtils.getUserNameFromJwtToken(token))?.let { user ->
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(user, null, user.authorities).let {
                        it.details = WebAuthenticationDetailsSource().buildDetails(request)
                        it
                    }
            }
        } catch (e: Exception) {
            logger.error("Cannot set user authentication: {}", e)
        }
        filterChain.doFilter(request, response)
    }

}
