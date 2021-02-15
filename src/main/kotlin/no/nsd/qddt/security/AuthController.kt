package no.nsd.qddt.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*


@CrossOrigin(origins = arrayOf("*"), maxAge = 3600)
@RestController
class AuthenticationController {

//    https://proandroiddev.com/how-to-create-a-rest-api-for-your-app-with-spring-boot-kotlin-gradle-part-2-security-with-32f944918fe1
    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var jwtUtil: JwtUtils


    companion object {
        private const val SIGNIN_URL = "auth/signin"
        private val logger: Logger = LoggerFactory.getLogger(AuthenticationController::class.java)
//        private const val REFRESH_TOKEN_URL = "auth/token/refresh"
    }

    @RequestMapping(value = [SIGNIN_URL], method = [RequestMethod.POST])
    fun authenticateUser(@RequestBody userForm: UserForm): ResponseEntity<*>? {
        with (authenticationManager.authenticate(UsernamePasswordAuthenticationToken(userForm.email,userForm.password))) {
            SecurityContextHolder.getContext().authentication = this
                logger.info(this.toString())

            return ResponseEntity.ok(jwtUtil.generateJwtToken(this))
        }
    }


}
