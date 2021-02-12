package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.*
import java.sql.Timestamp

/**
 * @author Stig Norland
 */
@Entity
@Table(name="user_account")
class User (
    @Id  @GeneratedValue
    val id: UUID,

    var email : String,

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference(value = "agentRef")
    @JoinColumn(name = "agency_id")
    var agency : Agency,

    @ManyToMany(fetch = FetchType.EAGER)
    val authority: MutableCollection<Authority>,

    @Version
    val modified: Timestamp? = null,

    @JsonIgnore private val password : String,


    ): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authority.map {
            GrantedAuthority { it.authority }
        }.toMutableList()
    }

    override fun getPassword(): String {
        return password
    }


    override fun getUsername(): String {
        return username
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
        return isEnabled
    }


}
