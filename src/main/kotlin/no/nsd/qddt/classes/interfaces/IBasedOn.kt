package no.nsd.qddt.classes.interfaces

import no.nsd.qddt.classes.AbstractEntityAudit.ChangeKind
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
    val isNewCopy get() = (changeKind == ChangeKind.NEW_COPY || id == null && changeKind != ChangeKind.CREATED)
}
