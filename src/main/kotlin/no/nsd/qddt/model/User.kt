package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.repository.handler.AgentAuditTrailListener
import no.nsd.qddt.repository.handler.UserAuditTrailListener
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Cacheable
@Entity
@Table(name="user_account")
@EntityListeners(value = [UserAuditTrailListener::class])
data class User(

    private var username : String = "?",

    @Column(insertable = false, updatable = false)
    var agencyId: UUID? = null

):UserDetails {

    @Id  @GeneratedValue lateinit var id: UUID

    @JsonIgnore
    @Version
    lateinit var modified: Timestamp

    lateinit var email : String

    override fun getUsername(): String {
        return username
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "agencyId")
    lateinit var agency : Agency


//    override fun toString(): String {
//        return "User(email='$email', id=$id, modified=$modified, agencyId=${agencyId?:"?"}, authority=${getAuthority()}, hasPassword='${password.isNotBlank()}', username='$username', isEnabled=$isEnabled)"
//    }

    fun getAuthority(): String {
        return authorities.joinToString { it.authority }
    }

}
