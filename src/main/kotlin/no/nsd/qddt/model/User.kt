package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonBackReference
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
class User (

    @Id  @GeneratedValue
    @Column(updatable = false, nullable = false)
    var id: UUID?=null,

    var email : String,

    private  var username : String,

    @JsonIgnore
    private var password : String,

    private  var isEnabled: Boolean=false,

    @Version
    val modified: Timestamp? = null,


    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference(value = "agentRef")
    @JoinColumn(name = "agency_id")
    var agency : Agency,

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    protected var authority: MutableCollection<Authority>

    ): UserDetails {

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

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authority.map {
            GrantedAuthority { it.authority }
        }.toMutableList()
    }

    override fun toString(): String {
        return "User(email='$email', id=$id, modified=$modified, agency=${agency.name}, authority=${authority.joinToString(" + ") { it.authority }}, hasPassword='${password.isNotBlank()}', username='$username', isEnabled=$isEnabled)"
    }


}
