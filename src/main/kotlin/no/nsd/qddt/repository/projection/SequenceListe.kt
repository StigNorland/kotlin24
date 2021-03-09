package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Sequence
import org.springframework.data.rest.core.config.Projection

@Projection(name = "sequenceListe", types = [Sequence::class])
interface SequenceListe: IAbstractEntityViewList
