package no.nsd.qddt.classes.interfaces

import java.util.*

/**
 * @author Stig Norland
 */
interface IParameter {
    var id: UUID?
    var name: String?
    var referencedId: UUID?
}
