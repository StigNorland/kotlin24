package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.embedded.Version
import org.springframework.beans.factory.annotation.Value
import java.util.*

/**
 * @author Stig Norland
 */
interface IAbstractEntityViewList {
    var id: UUID

    var name: String

    var version: Version

    var xmlLang: String

    var classKind: String


    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long

}
