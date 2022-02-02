package no.nsd.qddt.config

import no.nsd.qddt.model.User
import org.hibernate.envers.DefaultRevisionEntity
import org.hibernate.envers.RevisionEntity
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * @author Stig Norland
 */
@Entity
@Table( name = "revinfo")
@RevisionEntity(AuditRevisionListener::class)
class RevisionEntityImpl : DefaultRevisionEntity() {

    @ManyToOne
    lateinit var modifiedBy: User

    lateinit var modified: Timestamp
}
