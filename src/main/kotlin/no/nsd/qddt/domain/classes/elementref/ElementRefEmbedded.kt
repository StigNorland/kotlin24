package no.nsd.qddt.domain.classes.elementref

import no.nsd.qddt.domain.classes.interfaces.IWebMenuPreview
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable

/**
 * @author Stig Norland
 */
@Embeddable
class ElementRefEmbedded<T : IWebMenuPreview?>(kind: ElementKind, id: UUID, rev: Int?) : AbstractElementRef<T>(kind,id,rev), Serializable {

    public override fun clone(): ElementRefEmbedded<T> {
        return ElementRefEmbedded<T>(elementKind, elementId, elementRevision).apply {
            this.version = version
            this.name =name
        }
    }

}
