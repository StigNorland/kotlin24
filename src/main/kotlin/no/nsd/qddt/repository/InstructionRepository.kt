package no.nsd.qddt.repository

import no.nsd.qddt.model.Instruction
import no.nsd.qddt.repository.projection.InstructionListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */

@RepositoryRestResource(path = "instruction", itemResourceRel = "Instruction", excerptProjection = InstructionListe::class)
interface InstructionRepository : BaseMixedRepository<Instruction>
{
    @Query(
        value = "SELECT ca.* FROM instruction ca WHERE " +
                "( ca.xml_lang ILIKE :xmlLang " +
                "AND ca.description ILIKE searchStr(:description))" ,
        countQuery = "SELECT count(ca.*) FROM instruction ca WHERE " +
                "( ca.xml_lang ILIKE :xmlLang " +
                "AND ca.description ILIKE searchStr(:description))" ,
        nativeQuery = true
    )
    fun findByQuery(
        @Param("xmlLang") xmlLang: String?,
        @Param("description") description: String?,
        pageable: Pageable?
    ): Page<Instruction>

//    @RestResource(rel = "description", path = "findByQuery")
//    fun findByDescriptionIgnoreCaseLikeAndXmlLangLike( description: String, xmlLang: String,  pageable: Pageable): Page<Instruction>

}
