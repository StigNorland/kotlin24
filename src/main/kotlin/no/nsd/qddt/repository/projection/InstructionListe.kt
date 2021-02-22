package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Instruction
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "instructionListe", types = [Instruction::class])
interface InstructionListe {
    var id:UUID
    var name: String
    var description: String
}
