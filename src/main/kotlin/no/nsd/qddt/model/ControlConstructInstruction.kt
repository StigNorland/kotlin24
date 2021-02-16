package no.nsd.qddt.model

import no.nsd.qddt.model.classes.ControlConstructInstructionRank
import org.hibernate.envers.Audited
import java.io.Serializable
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Audited
@Embeddable
class ControlConstructInstruction : Serializable {
    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE], fetch = FetchType.EAGER)
    @JoinColumn(name = "instruction_id")
    lateinit var instruction: Instruction

    @Enumerated(EnumType.STRING)
    lateinit var instructionRank: ControlConstructInstructionRank
    
}
