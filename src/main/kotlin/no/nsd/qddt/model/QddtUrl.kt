package no.nsd.qddt.model

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * @author Stig Norland
 */
@Entity
@Table(name = "uuidpath")
class QddtUrl {
    @Type(type = "pg-uuid")
    @Id
    var id: UUID? = null

    var path: String? = null
    var name: String? = null

    @Column(name = "modified_by_id")
    var userId: UUID? = null

    val url: String
        get() = path + "/" + id.toString()

}