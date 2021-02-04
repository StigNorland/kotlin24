package no.nsd.qddt.domain.controlconstruct.pojo

import no.nsd.qddt.domain.instruction.Instruction
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
    var instruction: Instruction? = null

    @Enumerated(EnumType.STRING)
    var instructionRank: ControlConstructInstructionRank? = null
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is ControlConstructInstruction) return false
        val that = o
        return (if (instruction != null) instruction == that.instruction else that.instruction == null) && instructionRank == that.instructionRank
    }

    override fun hashCode(): Int {
        var result = if (instruction != null) instruction.hashCode() else 0
        result = 31 * result + if (instructionRank != null) instructionRank.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "ControlConstructInstruction{" +
                ", instruction=" + instruction +
                ", instructionRank=" + instructionRank +
                '}'
    }

    companion object {
        private const val serialVersionUID = -7261847559839337877L
    }
}
