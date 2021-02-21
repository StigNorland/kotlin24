package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Instrument
import org.springframework.data.rest.core.config.Projection

@Projection(name = "instrumentListe", types = [Instrument::class])
interface InstrumentListe: IAbstractEntityEditList
