package no.nsd.qddt.domain.classes.elementref

import no.nsd.qddt.domain.classes.interfaces.IWebMenuPreview
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable

/**
 * @author Stig Norland
 */
@Embeddable
class ElementRefEmbedded<T : IWebMenuPreview?> : AbstractElementRef<T>, Serializable {
    constructor() {}
    constructor(kind: ElementKind?, id: UUID?, rev: Int?) : super(kind, id, rev) {}

    public override fun clone(): ElementRefEmbedded<T?> {
        val retval = ElementRefEmbedded<T?>(elementKind, elementId, elementRevision)
        retval.version = version
        retval.setName(getName())
        return retval
    }

    companion object {
        private const val serialVersionUID = 3206987451754010936L
    }
}
