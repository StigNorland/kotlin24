package no.nsd.qddt.config

import no.nsd.qddt.model.User
import org.hibernate.envers.DefaultRevisionEntity
import org.hibernate.envers.RevisionEntity
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
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
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
class RevisionEntityImpl : DefaultRevisionEntity() {

    @ManyToOne
    @LastModifiedBy
    lateinit var modifiedBy: User

    @LastModifiedDate
    lateinit var modified: Timestamp
}
