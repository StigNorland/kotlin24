package no.nsd.qddt.model.interfaces

import java.util.*

/**
 * @author Stig Norland
 */
interface IParameter {
    var id: UUID?
    var name: String?
    var referencedId: UUID?
}
