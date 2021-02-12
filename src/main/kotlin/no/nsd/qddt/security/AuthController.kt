package no.nsd.qddt.security

import no.nsd.qddt.config.JwtTokenUtil
import no.nsd.qddt.config.JwtUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import no.nsd.qddt.model.User


@RestController
class AuthenticationController {

//    https://proandroiddev.com/how-to-create-a-rest-api-for-your-app-with-spring-boot-kotlin-gradle-part-2-security-with-32f944918fe1
    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var jwtUtil: JwtTokenUtil

    @Autowired
    private lateinit var userService: JwtUserDetailsService

//    @RequestMapping(value = [SIGNIN_URL], method = arrayOf(RequestMethod.POST))
//    @Throws(AuthenticationException::class)
//    fun register(@RequestBody loginUser: UserForm): ResponseEntity<*> {
//
//        val authentication = authenticationManager!!.authenticate(
//            UsernamePasswordAuthenticationToken(
//                loginUser.username,
//                loginUser.password
//            )
//        )
//
//        SecurityContextHolder.getContext().authentication = authentication
//        val user = userService?.loadUserByUsername(loginUser.username!!)
//        val token = user?.let { jwtTokenUtil!!.generateToken(it) }
//        return ResponseEntity.ok(token!!)
//    }

    companion object {
        private const val SIGNIN_URL = "auth/signin"
        private const val REFRESH_TOKEN_URL = "auth/token/refresh"
    }

    /**
     * Returns authentication token for given user
     * @param authenticationRequest with username and password
     * @return generated JWT
     * @throws AuthenticationException if token is invalid
     */
    @RequestMapping(value = [SIGNIN_URL], method = [RequestMethod.POST])
    @Throws(AuthenticationException::class)
    fun getAuthenticationToken(@RequestBody userForm: UserForm): ResponseEntity<*>? {
        val userDetails: User =
            with(userService) { loadUserByUsername(userForm.email!!) } as User

        SecurityContextHolder.getContext().authentication  = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                userForm.username,
                userForm.password
            )
        )

        val token = jwtUtil.generateToken(userDetails)
        return ResponseEntity.ok(token)
    }

    /**
     * Refreshes token
     * @param request with old JWT
     * @return Refreshed JWT
     */
    @RequestMapping(REFRESH_TOKEN_URL)
    fun refreshAuthenticationToken(request: HttpServletRequest?): ResponseEntity<*>? {
//        String token = request.getHeader(tokenHeader);
//        LOG.info("refreshAuthenticationToken");
        val refreshedToken: String =
            jwtUtil.generateToken(SecurityContextHolder.getContext().authentication.details as UserDetails)
        return ResponseEntity.ok<Any>(refreshedToken)
    }

}
