package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.embedded.Version
import java.util.*

interface IInstruction {
    var id: UUID
    var description: String
    var version:Version

}