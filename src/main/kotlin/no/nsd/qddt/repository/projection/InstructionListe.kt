package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Instruction
import org.springframework.data.rest.core.config.Projection

@Projection(name = "nstructionListe", types = [Instruction::class])
interface InstructionListe {
    var name: String
    var description: String
}
