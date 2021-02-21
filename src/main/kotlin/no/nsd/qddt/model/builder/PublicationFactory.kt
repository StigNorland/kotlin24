package no.nsd.qddt.model.builder

import no.nsd.qddt.model.Publication
import no.nsd.qddt.model.embedded.PublicationElement
import no.nsd.qddt.model.interfaces.IEntityFactory
import kotlin.streams.toList

/**
 * @author Stig Norland
 */
class PublicationFactory : IEntityFactory<Publication> {
    override fun create(): Publication {
        return Publication()
    }

    override fun copyBody(source: Publication, dest: Publication): Publication {
        dest.purpose = source.purpose
        dest.status = source.status
        dest.publicationElements = source.publicationElements.stream().map { it.clone() }.toList() as MutableList<PublicationElement>
        return dest
    }
}
