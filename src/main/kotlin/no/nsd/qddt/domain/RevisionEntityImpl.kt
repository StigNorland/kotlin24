package no.nsd.qddt.domain

import no.nsd.qddt.domain.user.User
import org.hibernate.envers.DefaultRevisionEntity
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.ManyToOne

/**
 * @author Stig Norland
 */
@Entity(name = "revinfo") //@RevisionEntity( RevisionEntityListenerImpl.class )
class RevisionEntityImpl : DefaultRevisionEntity() {
    @ManyToOne
    @LastModifiedBy
    lateinit var modifiedBy: User

    @LastModifiedDate
    lateinit var modified: Timestamp
}
