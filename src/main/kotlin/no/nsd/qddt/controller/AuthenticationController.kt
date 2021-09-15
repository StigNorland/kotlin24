package no.nsd.qddt.controller

import no.nsd.qddt.security.AuthTokenUtil
import no.nsd.qddt.security.AuthUserDetailsService
import no.nsd.qddt.security.UserForm
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/login"])
class AuthenticationController {

    @Autowired
    lateinit var authUserDetailsService: AuthUserDetailsService

    @Autowired
    lateinit var jwtTokenUtil: AuthTokenUtil

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun authenticateUser(@RequestBody userForm: UserForm): ResponseEntity<*> {
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

        val isValid = isValidForLogin(admin)

        if (!isValid.isNullOrBlank()) {
            return getFailureResponse(isValid)
        }

        val status = adminService.doLogin(admin)

        when (status) {
            -1 -> {
                return getFailureResponse("Username Not exists")
            }
            -2 -> {
                // This should never happen
                return getFailureResponse("Password not matching the username")
            }
        }

        return getUserWithAuthCredentials(admin.userName)
    }

    /**
     * Should call only this method when user successfully SignUp/SignIn
     */
    fun getUserWithAuthCredentials(userName: String): ResponseEntity<*> {

        val localAdmin = adminService.getAdmin(userName)!!

        return getSuccessResponse(UserWithKey(localAdmin, JwtAuthenticationResponse(jwtTokenUtil.generateToken(localAdmin.userName, 2))))
    }
    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)
}
