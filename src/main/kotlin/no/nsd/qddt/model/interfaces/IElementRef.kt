package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.classes.Version
import no.nsd.qddt.model.classes.elementref.ElementKind
import java.io.Serializable
import java.util.*

/**
 * @author Stig Norland
 */
interface IElementRef<T : IWebMenuPreview> : Cloneable, Serializable {
    var name: String?
    var version: Version
    var elementId: UUID
    var elementRevision: Int?
    var elementKind: ElementKind
    var element: T?
}
