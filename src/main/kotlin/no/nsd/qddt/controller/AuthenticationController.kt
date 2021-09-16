package no.nsd.qddt.controller

import no.nsd.qddt.model.User
import no.nsd.qddt.security.AuthTokenUtil
import no.nsd.qddt.security.UserForm
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/login"])
class AuthenticationController {

    //    https://proandroiddev.com/how-to-create-a-rest-api-for-your-app-with-spring-boot-kotlin-gradle-part-2-security-with-32f944918fe1
    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var jwtUtil: AuthTokenUtil

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun authenticateUser(@RequestBody userForm: UserForm): ResponseEntity<*> {
        return try {
            val authenticate =
                if (userForm.email != null)
                    authenticationManager.authenticate(UsernamePasswordAuthenticationToken(userForm.email,userForm.password))
                else
                    authenticationManager.authenticate(UsernamePasswordAuthenticationToken(userForm.username,userForm.password))
            val user = authenticate.principal as User
            logger.info("User logged in {}", user.toString())
            SecurityContextHolder.getContext().authentication = authenticate
            ResponseEntity.ok(jwtUtil.generateJwtToken(authenticate))
        } catch (ex: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Any>()
        }
        catch (ex: Exception) {
            logger.error(ex.localizedMessage)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build<Any>()
        }

    }

    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)

//    @Value("\${jwt.header}")
//    private val tokenHeader: String? = null
//
//    @Autowired
//    lateinit var jwtTokenUtil: AuthTokenUtil
//
//    @Autowired
//    lateinit var authenticationManager: AuthenticationManager
//
//    @Autowired
//    lateinit var userDetailsService: AuthUserDetailsService
//
//
//    @PostMapping
//    @Throws(AuthenticationException::class)
//    fun createAuthenticationToken(@RequestBody authenticationRequest: JwtAuthenticationRequest, device: Device): ResponseEntity<*> {
//
//        // Perform the security
//        val authentication = authenticationManager.authenticate(
//            UsernamePasswordAuthenticationToken(
//                authenticationRequest.username,
//                authenticationRequest.password)
//        )
//
//        SecurityContextHolder.getContext().authentication = authentication
//
//        // Reload password post-security so we can generate token
//        val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username)
//        val token = jwtTokenUtil.generateToken(userDetails, device)
//        val result = HashMap<String,Any>()
//        result.put("token", token)
//        val currentUser = (userDetails as JwtUser).user
//
//        currentUser?.let { result.put("user", it) }
//
//        return ResponseEntity.ok(result)
//    }
//
//
//    @GetMapping("")
//    fun refreshAndGetAuthenticationToken(request: HttpServletRequest): ResponseEntity<*> {
//        val token = request.getHeader(tokenHeader)
//        val username = jwtTokenUtil.getUsernameFromToken(token)
//        val user = userDetailsService.loadUserByUsername(username) as JwtUser
//
//        if (jwtTokenUtil.canTokenBeRefreshed(token, user.lastPasswordResetDate)) {
//            val refreshedToken = jwtTokenUtil.refreshToken(token)
//            return ResponseEntity.ok(JwtAuthenticationResponse(refreshedToken))
//        } else {
//            return ResponseEntity.badRequest().body<Any>(null)
//        }
//    }
//
//    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun authenticateUser(@RequestBody userForm: UserForm): ResponseEntity<*> {
//        return try {
//
//            val authenticate = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(userForm.email,userForm.password))
//            val user: User = authenticate.principal as User
//            logger.info("User logged in {}", user.toString())
//            SecurityContextHolder.getContext().authentication = authenticate
//            ResponseEntity.ok(AuthTokenUtil().generateJwtToken(authenticate))
////                .header(HttpHeaders.AUTHORIZATION,jwtUtil.generateJwtToken(authenticate) )
////                .body(jwtUtil.generateJwtToken(authenticate))
//        } catch (ex: BadCredentialsException) {
//            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Any>()
//        }
//
//    }

//        val isValid = isValidForLogin(admin)
//
//        if (!isValid.isNullOrBlank()) {
//            return getFailureResponse(isValid)
//        }
//
//        val status = adminService.doLogin(admin)
//
//        when (status) {
//            -1 -> {
//                return getFailureResponse("Username Not exists")
//            }
//            -2 -> {
//                // This should never happen
//                return getFailureResponse("Password not matching the username")
//            }
//        }
//
//        return getUserWithAuthCredentials(admin.userName)
//    }
//
//    /**
//     * Should call only this method when user successfully SignUp/SignIn
//     */
//    fun getUserWithAuthCredentials(userName: String): ResponseEntity<*> {
//
//        val localAdmin = adminService.getAdmin(userName)!!
//
//        return getSuccessResponse(UserWithKey(localAdmin, JwtAuthenticationResponse(jwtTokenUtil.generateToken(localAdmin.userName, 2))))
//    }
//    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)
}
