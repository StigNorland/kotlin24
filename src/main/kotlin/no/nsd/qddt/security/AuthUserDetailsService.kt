package no.nsd.qddt.security

//import no.nsd.qddt.repository.UserRepository
import no.nsd.qddt.model.User
import no.nsd.qddt.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*


/**
 * @author Stig Norland
 */
@Service
class AuthUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(name: String): User? {
        return userRepository.findByEmailIgnoreCase(name.lowercase(Locale.getDefault()).trim())
    }
}

//interface JwtUserDetailsRepository : CrudRepository<User, UUID> {
//    fun findByEmailIgnoreCase(email: String)
//}
