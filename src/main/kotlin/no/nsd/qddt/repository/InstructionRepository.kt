package no.nsd.qddt.repository

import no.nsd.qddt.model.Instruction
import no.nsd.qddt.model.Universe
import no.nsd.qddt.repository.projection.InstructionListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.*

/**
 * @author Stig Norland
 */

@RepositoryRestResource(path = "instruction", itemResourceRel = "Instruction", excerptProjection = InstructionListe::class)
interface InstructionRepository : JpaRepository<Instruction,UUID>
{
    @RestResource(rel = "description", path = "findBy")
    fun findByDescriptionIgnoreCaseLikeAndXmlLangLike( description: String, xmlLang: String,  pageable: Pageable): Page<Instruction>

}
