package no.nsd.qddt.repository

import no.nsd.qddt.model.Instruction
import no.nsd.qddt.repository.projection.InstructionListe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */

@RepositoryRestResource(path = "instruction", collectionResourceRel = "items", itemResourceRel = "Instruction", excerptProjection = InstructionListe::class)
interface InstructionRepository : JpaRepository<Instruction,UUID>
