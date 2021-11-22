package no.nsd.qddt.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.enums.ElementKind
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

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