package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.ElementKind
import java.io.Serializable
import java.util.*

/**
 * @author Stig Norland
 */
interface IElementRef<T : IWebMenuPreview> : Cloneable, Serializable {
    var elementId: UUID?
    var elementRevision: Int?
    var elementKind: ElementKind
    var name: String?
    var version: Version
    var element: T?

//    fun getUri() : UriId {
//        return UriId.fromAny("${elementId}:${elementRevision?:0}")
//    }
}
