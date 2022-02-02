package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Instruction
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "instructionListe", types = [Instruction::class])
interface InstructionListe:IAbstractEntityViewList  {
    var description: String

    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String
}
