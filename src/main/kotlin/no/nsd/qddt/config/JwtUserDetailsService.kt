package no.nsd.qddt.config

//import no.nsd.qddt.domain.user.UserRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*


/**
 * @author Stig Norland
 */
@Service
class JwtUserDetailsService(private val userRepository: JwtUserDetailsRepository) : UserDetailsService {
//    @Autowired
//    private lateinit var userRepository: UserRepository

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(name: String): UserDetails {
        return userRepository.findByEmailIgnoreCase(name.toLowerCase().trim())
    }
}

interface JwtUserDetailsRepository : CrudRepository<UserDetails, UUID> {
    fun findByEmailIgnoreCase(email: String) : UserDetails
}
