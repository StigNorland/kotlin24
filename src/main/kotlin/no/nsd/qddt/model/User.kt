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
class User (
    @Id  @GeneratedValue
    val id: UUID?=null,

    @Version
    val modified: Timestamp?=null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    var agency : Agency?=null,


    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    internal var authority: MutableCollection<Authority> = mutableListOf(),

    private  var isEnabled: Boolean=false,

    private var username : String? = null,

    @JsonIgnore
    private  var password : String? =null


): UserDetails {

    lateinit var email : String


    @Column( name="agency_id", updatable = false, insertable = false)
    val angencyId: UUID?=null

    override fun getPassword(): String {
        return password?:"?"
    }

    override fun getUsername(): String {
        return username?:"?"
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

//    override fun toString(): String {
//        return "User(email='$email', id=$id, modified=$modified, agency=${agency.name}, authority=${authority.joinToString(" + ") { it.authority }}, hasPassword='${password.isNotBlank()}', username='$username', isEnabled=$isEnabled)"
//    }


}
