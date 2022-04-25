package no.nsd.qddt.model.classes

import no.nsd.qddt.model.Instruction
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.interfaces.IInstruction
import java.util.*

class IInstructionImpl() : IInstruction, java.io.Serializable {
    override lateinit var id: UUID
    override lateinit var description: String
    override lateinit var version: Version

    constructor(instruction: Instruction) : this() {
        id = instruction.id!!
        description = instruction.description
        version = instruction.version
    }
}