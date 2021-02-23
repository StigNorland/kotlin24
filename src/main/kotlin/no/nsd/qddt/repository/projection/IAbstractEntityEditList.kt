package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.embedded.Version
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

    @Value(value = "#{target.modifiedBy.username  + '@' + target.modifiedBy.agency.name  }")
    fun getModifiedBy(): String?

    var version: Version
//    @Value(value = "#{target.version.toString() }")
//    fun getVersion(): String?

    var xmlLang: String

    var classKind: String
}
