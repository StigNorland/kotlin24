package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Table(name="user_account")
data class User(private var username : String = "?"):UserDetails {

    @Id  @GeneratedValue lateinit var id: UUID

    @Version
    lateinit var modified: Timestamp

    lateinit var email : String

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

    private var isEnabled: Boolean = false
    override fun isEnabled(): Boolean {
        return isEnabled
    }

    @JsonIgnore
    private lateinit var password : String
    override fun getPassword(): String {
        return password
    }


    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    private lateinit var authorities: MutableCollection<Authority>
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities.map {
            GrantedAuthority { it.authority }
        }.toMutableSet()
    }


    @Column(insertable = false, updatable = false)
    var agencyId: UUID? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agencyId")
    lateinit var agency : Agency


//    override fun toString(): String {
//        return "User(email='$email', id=$id, modified=$modified, agency=${agency.name}, authority=${authority.joinToString(" + ") { it.authority }}, hasPassword='${password.isNotBlank()}', username='$username', isEnabled=$isEnabled)"
//    }

    fun getAuthority(): String {
        return authorities.joinToString { it.authority }
    }

}
