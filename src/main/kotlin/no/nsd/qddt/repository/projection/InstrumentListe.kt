package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Instrument
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "instrumentListe", types = [Instrument::class])
interface InstrumentListe: IAbstractEntityViewList {

    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String
}
