package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.classes.AbstractEntityAudit.ChangeKind
import java.util.*

/**
 * @author Stig Norland
 */
interface IBasedOn:IDomainObject {

    var basedOnObject: UUID?
    var basedOnRevision: Int?

    var changeKind: ChangeKind
    var changeComment: String

    val isBasedOn get() = changeKind == ChangeKind.BASED_ON || changeKind == ChangeKind.NEW_COPY || changeKind == ChangeKind.TRANSLATED || changeKind == ChangeKind.REFERENCED
    val isNewCopy get() = (changeKind == ChangeKind.NEW_COPY || version.revision == 0 && changeKind != ChangeKind.CREATED)
}
