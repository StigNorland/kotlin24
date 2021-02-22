package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.Agency
import no.nsd.qddt.model.User
import java.sql.Timestamp

/**
 * @author Stig Norland
 */
interface IDomainObject : IWebMenuPreview, IXmlBuilder {
    // val id: UUID
    // var name: String
    // var version: Version
    var modified: Timestamp
    var modifiedBy: User
    var agency: Agency
    var classKind: String
    // fun xmlBuilder(): AbstractXmlBuilder?
}
