package no.nsd.qddt.model.embedded

import no.nsd.qddt.model.classes.AbstractElementRef
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import org.hibernate.envers.Audited
import java.io.Serializable
import javax.persistence.Embeddable

/**
 * @author Stig Norland
 */

@Audited
@Embeddable
class ElementRefEmbedded<T : IWebMenuPreview>:AbstractElementRef<T>, Serializable {

    constructor() : super()

    constructor(entity: T) : super(entity)

    constructor(elementKind: ElementKind, uriId: UriId):super(elementKind,uriId)



    public override fun clone(): ElementRefEmbedded<T> {
        return ElementRefEmbedded<T>(elementKind, uri).apply {
            this.version = version
            this.name =name
        }
    }

}
