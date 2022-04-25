package no.nsd.qddt.model

import no.nsd.qddt.model.embedded.UriId
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
class ControlConstructInstruction : Serializable {

    @Enumerated(EnumType.STRING)
    lateinit var instructionRank: InstructionRank

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "instruction_id", nullable = true)),
        AttributeOverride(name = "rev", column = Column(name = "instruction_rev", nullable = true)),
    )
    lateinit var uri: UriId

    @Transient
    var instruction:Instruction?=null


}
