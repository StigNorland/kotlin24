package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Sequence
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "sequenceListe", types = [Sequence::class])
interface SequenceListe: IAbstractEntityViewList{
    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String
}
