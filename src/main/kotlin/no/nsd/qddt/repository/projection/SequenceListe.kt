package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Sequence
import no.nsd.qddt.model.enums.SequenceKind
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "sequenceListe", types = [Sequence::class])
interface SequenceListe: IAbstractEntityViewList{
    var description: String

    var sequenceKind: SequenceKind

    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String
}
