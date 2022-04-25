package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Universe
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

/**
 * @author Stig Norland
 */
@Projection(name = "universeListe", types = [Universe::class])
interface UniverseListe:IAbstractEntityViewList {


    var description:String

    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String
}

