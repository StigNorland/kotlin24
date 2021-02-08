package no.nsd.qddt.domain.classes.interfaces

import no.nsd.qddt.domain.classes.elementref.ElementKind
import java.util.*

/**
 * @author Stig Norland
 */
interface IElementRef<T : IWebMenuPreview?> : Cloneable {
    var name: String?
    var version: Version
    var elementId: UUID
    var elementRevision: Int?
    var elementKind: ElementKind
    var element: T?
}
