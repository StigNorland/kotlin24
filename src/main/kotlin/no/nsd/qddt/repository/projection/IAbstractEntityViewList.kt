package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.embedded.Version
import org.springframework.beans.factory.annotation.Value
import java.util.*

/**
 * @author Stig Norland
 */
interface IAbstractEntityViewList {

    var name: String

    var xmlLang: String

    var classKind: String

    var id: UUID

    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long


    @Value(value = "#{target.modifiedBy.username  + '@' + target.modifiedBy.agency?.name }")
    fun getModifiedBy(): String?

    var agencyId: UUID?

    //    var version: Version
    @Value(value = "#{target.version.toString() }")
    fun getVersion(): String?

}
