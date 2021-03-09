package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Concept
import org.springframework.data.rest.core.config.Projection

@Projection(name = "conceptListe", types = [Concept::class])
interface ConceptListe: IAbstractEntityViewList
