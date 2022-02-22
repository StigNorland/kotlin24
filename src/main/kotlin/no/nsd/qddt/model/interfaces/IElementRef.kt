package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.ElementKind
import java.io.Serializable
import java.util.*

/**
 * @author Stig Norland
 */
interface IElementRef<T : IWebMenuPreview> : Cloneable, Serializable {
    var uri: UriId
    var elementKind: ElementKind
    var version: Version
    var name: String?
    var element: T?

//    fun getUri() : UriId {
//        return UriId.fromAny("${elementId}:${elementRevision?:0}")
//    }
}
