package no.nsd.qddt.model

import no.nsd.qddt.model.enums.InstructionRank
import org.hibernate.envers.Audited
import java.io.Serializable
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Cacheable
@Audited
@Embeddable
class ControlConstructInstruction() : Serializable {

    @ManyToOne(optional=false, fetch = FetchType.EAGER)
    lateinit var instruction: Instruction

    @Enumerated(EnumType.STRING)
    lateinit var instructionRank: InstructionRank
    
}
