package no.nsd.qddt.repository.projection

import org.springframework.beans.factory.annotation.Value
import java.sql.Timestamp
import java.util.*

/**
 * @author Stig Norland
 */
interface IAbstractEntityEditList {
    var id: UUID
    var name: String

    var modified: Timestamp

    @Value(value = "#{target.modifiedBy.username  + '@' + target.modifiedBy.agency  }")
    fun getModifiedBy(): String?

    @Value(value = "#{target.version.toString() }")
    fun getVersion(): String?

    var xmlLang: String
}
