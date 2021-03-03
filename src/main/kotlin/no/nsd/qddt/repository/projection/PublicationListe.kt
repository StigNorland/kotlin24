package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Publication
import org.springframework.data.rest.core.config.Projection

@Projection(name = "publicationListe", types = [Publication::class])
interface PublicationListe: IAbstractEntityEditList
