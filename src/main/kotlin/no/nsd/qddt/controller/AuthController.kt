package no.nsd.qddt.controller

import no.nsd.qddt.model.User
import no.nsd.qddt.security.AuthTokenUtil
import no.nsd.qddt.security.UserForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController





@RestController
@RequestMapping(path = ["/login"])
class AuthenticationController {

//    https://proandroiddev.com/how-to-create-a-rest-api-for-your-app-with-spring-boot-kotlin-gradle-part-2-security-with-32f944918fe1
    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var jwtUtil: AuthTokenUtil


//    companion object {
//        private val logger: Logger = LoggerFactory.getLogger(AuthenticationController::class.java)
//    }

    @PostMapping
    fun authenticateUser(@RequestBody userForm: UserForm): ResponseEntity<*> {
        return try {

            val authenticate = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(userForm.email,userForm.password))
            val user: User = authenticate.principal as User
            SecurityContextHolder.getContext().authentication = authenticate
            ResponseEntity.ok()
//                .header(HttpHeaders.AUTHORIZATION,jwtUtil.generateJwtToken(authenticate) )
                .body(jwtUtil.generateJwtToken(authenticate))
        } catch (ex: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Any>()
        }

//        with (authenticationManager.authenticate(UsernamePasswordAuthenticationToken(userForm.email,userForm.password))) {
//            SecurityContextHolder.getContext().authentication = this
//
//            return ResponseEntity.ok(jwtUtil.generateJwtToken(this))
//        }
    }


}
