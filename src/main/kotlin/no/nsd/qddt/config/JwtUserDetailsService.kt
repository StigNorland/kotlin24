package no.nsd.qddt.config

//import no.nsd.qddt.repository.UserRepository
import no.nsd.qddt.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


/**
 * @author Stig Norland
 */
@Service
class JwtUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
//    @Autowired
//    private lateinit var userRepository: UserRepository

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(name: String): UserDetails {
        return userRepository.findByEmailIgnoreCase(name.toLowerCase().trim()) as UserDetails
    }
}

//interface JwtUserDetailsRepository : CrudRepository<User, UUID> {
//    fun findByEmailIgnoreCase(email: String)
//}
