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
class User {

    @Id  @GeneratedValue lateinit var id: UUID

    @Version
    lateinit var modified: Timestamp


    lateinit var username : String

    lateinit var email : String

    var isEnabled: Boolean? = false

    @JsonIgnore
    lateinit var password : String

    @Column(insertable = false, updatable = false)
    var agencyId: UUID? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agencyId")
    lateinit var agency : Agency


    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    lateinit var authority: MutableSet<Authority>


//    override fun toString(): String {
//        return "User(email='$email', id=$id, modified=$modified, agency=${agency.name}, authority=${authority.joinToString(" + ") { it.authority }}, hasPassword='${password.isNotBlank()}', username='$username', isEnabled=$isEnabled)"
//    }


}
