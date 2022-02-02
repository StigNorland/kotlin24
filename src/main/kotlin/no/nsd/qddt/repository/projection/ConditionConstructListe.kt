package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.ConditionConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "conditionConstructListe", types = [ConditionConstruct::class])
interface ConditionConstructListe: IAbstractEntityViewList {

    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String
}
