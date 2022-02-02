package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.ControlConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "controlconstructListe", types = [ControlConstruct::class])
interface ControlConstructListe: IAbstractEntityViewList {
    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String
}

