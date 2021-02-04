package no.nsd.qddt.config

/**
 * @author Stig Norland
 */
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtRequestFilter : OncePerRequestFilter() {

    @Autowired
    private lateinit var myUserService: JwtUserDetailsService

    @Autowired
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @Value("\${auth.header}")
    private lateinit var  tokenHeader: String

    /**
     * Checks if JWT present and valid
     *
     * @param request  with JWT
     * @param response
     * @param chain
     * @throws ServletException
     * @throws IOException
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var authToken = request.getHeader(this.tokenHeader)
        when {
            authToken != null && authToken.startsWith("Bearer ") -> authToken = authToken.substring(6).trim()
        }
        val username = jwtTokenUtil.getUsernameFromToken(authToken!!)
        when (SecurityContextHolder.getContext().authentication) {
            null -> {
                val userDetails: UserDetails = this.myUserService.loadUserByUsername(username.toLowerCase().trim())
                when {
                    jwtTokenUtil.validateToken(authToken, userDetails) -> {
                        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authentication
                    }
                }
            }
        }
        filterChain.doFilter(request, response)
    }

}

