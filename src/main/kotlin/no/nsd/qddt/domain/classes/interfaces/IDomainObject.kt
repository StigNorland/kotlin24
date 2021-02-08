package no.nsd.qddt.domain.classes.interfaces

import no.nsd.qddt.domain.agency.Agency
import no.nsd.qddt.domain.user.User
import java.sql.Timestamp

/**
 * @author Stig Norland
 */
interface IDomainObject : IWebMenuPreview, IXmlBuilder {
    // val id: UUID
    // var name: String
    // var version: Version
    var modified: Timestamp?
    var modifiedBy: User?
    var agency: Agency?
    var classKind: String
    // val xmlBuilder: AbstractXmlBuilder?
}
