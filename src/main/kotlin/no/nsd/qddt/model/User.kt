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
class User: UserDetails {

    @Id  @GeneratedValue lateinit var id: UUID

    @Version
    lateinit var modified: Timestamp

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agency_id")
    @JsonBackReference(value = "userAgencyRef")
    lateinit var agency : Agency


    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    internal lateinit var authority: MutableCollection<Authority>

    private  var isEnabled: Boolean=false

    private lateinit var username : String

    @JsonIgnore
    private lateinit var password : String

    lateinit var email : String


    @Column( name="agency_id", updatable = false, insertable = false)
    val angencyId: UUID?=null

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

//    override fun toString(): String {
//        return "User(email='$email', id=$id, modified=$modified, agency=${agency.name}, authority=${authority.joinToString(" + ") { it.authority }}, hasPassword='${password.isNotBlank()}', username='$username', isEnabled=$isEnabled)"
//    }


}
