package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.repository.handler.UserAuditTrailListener
import org.hibernate.Hibernate
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
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@EntityListeners(value = [UserAuditTrailListener::class])
data class User(

    private var username : String = "?",

    @Column(nullable = false)
    var agencyId: UUID? = null,

    @JsonSerialize
    @JsonFormat
        (shape = JsonFormat.Shape.NUMBER_INT)
    @Version
    var modified: Timestamp?=null

):UserDetails {

    @Id  @GeneratedValue lateinit var id: UUID

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

    private var isEnabled: Boolean = false
    override fun isEnabled(): Boolean {
        return isEnabled
    }

    @JsonIgnore
    private lateinit var password : String
    override fun getPassword(): String {
        return password
    }

    @ManyToMany(fetch = FetchType.EAGER)
    private var authorities: MutableCollection<Authority> = mutableListOf()


    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities.map {
            GrantedAuthority { it.authority }
        }.toMutableSet()
    }

//    fun setAuthorites(grantedAuthorities: MutableCollection<out GrantedAuthority>){
//        authorities = grantedAuthorities.map { it -> it.authority }
//    }


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "agencyId", insertable = false, updatable =  false)
    lateinit var agency : Agency


    fun getAuthority(): String {
        return authorities.joinToString() { it.authority }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as User

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , modified = $modified )"
    }

}
