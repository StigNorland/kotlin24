package no.nsd.qddt.domain.classes.interfaces

import no.nsd.qddt.domain.classes.elementref.ElementKind
import java.util.*

/**
 * @author Stig Norland
 */
interface IElementRef<T : IWebMenuPreview?> : Cloneable {
    var elementId: UUID?
    var elementRevision: Int?
    var version: Version?
    var elementKind: ElementKind?
    var name: String?
    fun getElement(): T?
    fun setElement(element: T?)
}
