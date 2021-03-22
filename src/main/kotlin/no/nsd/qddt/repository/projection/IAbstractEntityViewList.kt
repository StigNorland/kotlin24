package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.embedded.Version
import org.springframework.beans.factory.annotation.Value
import java.util.*

/**
 * @author Stig Norland
 */
interface IAbstractEntityViewList {
    var name: String

    var id: UUID

    @Value(value = "#{target.modifiedBy.id }")
    fun getModifiedById(): UUID?

    var agencyId: UUID?


    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long

    @Value(value = "#{target.modifiedBy.username  + '@' + target.modifiedBy.agency?.name }")
    fun getModifiedBy(): String?

    var version: Version

    var xmlLang: String

    var classKind: String

}
