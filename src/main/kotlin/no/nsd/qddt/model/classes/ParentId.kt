package no.nsd.qddt.model.classes

import no.nsd.qddt.model.ConceptHierarchy
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.interfaces.IParentRef
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import org.springframework.hateoas.EntityModel
import java.io.Serializable
import java.util.*

class ParentId(parent: ConceptHierarchy?): IParentRef, Serializable {
    override var id: UUID? = null
    override var name: String ="?"
    override var version: Version = Version()
    override var parentRef: IParentRef? = null

//    init {
//        if (parent != null) {
//            id = parent.id
//            name = parent.name
//            version = parent.version
//            parentRef = parent.getparentRef()?.content
//        }
//    }

}
