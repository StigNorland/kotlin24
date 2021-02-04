package no.nsd.qddt.config

import no.nsd.qddt.domain.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


/**
 * @author Stig Norland
 */
@Service
class JwtUserDetailsService : UserDetailsService {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(name: String): User {
        return userRepository.findByEmailIgnoreCase(name.toLowerCase().trim()) as User
    }
}
