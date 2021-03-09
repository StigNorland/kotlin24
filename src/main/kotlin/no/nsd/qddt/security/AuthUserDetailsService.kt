package no.nsd.qddt.security

//import no.nsd.qddt.repository.UserRepository
import no.nsd.qddt.model.User
import no.nsd.qddt.repository.UserRepository
import no.nsd.qddt.security.AuthTokenFilter.Companion.logger
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


/**
 * @author Stig Norland
 */
@Service
class AuthUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(name: String): UserDetails? {
        return userRepository.findByEmailIgnoreCase(name.toLowerCase().trim())?.let {
            object:UserDetails {
                override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
                    return it.authority.map {
                        GrantedAuthority { it.authority }
                    }.toMutableSet()                }

                override fun getPassword(): String {
                    return it.password
                }

                override fun getUsername(): String {
                    return it.username
                }

                override fun isAccountNonExpired(): Boolean {
                    return true
                }

                override fun isAccountNonLocked(): Boolean {
                    return true
                }

                override fun isCredentialsNonExpired(): Boolean {
                    return true
                }

                override fun isEnabled(): Boolean {
                    return  it.isEnabled?:false
                }
            }
        }
    }
}

//interface JwtUserDetailsRepository : CrudRepository<User, UUID> {
//    fun findByEmailIgnoreCase(email: String)
//}
