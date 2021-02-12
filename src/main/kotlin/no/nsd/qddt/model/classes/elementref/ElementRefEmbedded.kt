package no.nsd.qddt.model.classes.elementref

import no.nsd.qddt.model.interfaces.IWebMenuPreview
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable

/**
 * @author Stig Norland
 */
@Embeddable
class ElementRefEmbedded<T : IWebMenuPreview>(elementKind: ElementKind, elementId: UUID, elementRevision: Int?) :
    AbstractElementRef<T>(elementKind, elementId, elementRevision), Serializable {

    public override fun clone(): ElementRefEmbedded<T> {
        return ElementRefEmbedded<T>(elementKind, elementId, elementRevision).apply {
            this.version = version
            this.name =name
        }
    }

}
