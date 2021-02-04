package no.nsd.qddt.domain.classes.interfaces

import no.nsd.qddt.domain.agency.Agency
import no.nsd.qddt.domain.user.User
import java.sql.Timestamp

/**
 * @author Stig Norland
 */
interface IDomainObject : IWebMenuPreview, IXmlBuilder {
    var modifiedBy: User?
    var modified: Timestamp?
    var agency: Agency?
    var classKind: String?
}
