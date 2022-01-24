package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Publication
import no.nsd.qddt.model.embedded.Version
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "publicationListe", types = [Publication::class])
interface PublicationListe: IAbstractEntityViewList {
    var label: String
    var statusId: Long

}