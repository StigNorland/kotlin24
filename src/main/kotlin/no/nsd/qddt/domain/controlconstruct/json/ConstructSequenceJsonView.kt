package no.nsd.qddt.domain.controlconstruct.json

import no.nsd.qddt.domain.classes.elementref.ElementRefEmbedded
import no.nsd.qddt.domain.controlconstruct.pojo.ControlConstruct
import no.nsd.qddt.domain.controlconstruct.pojo.Sequence
import no.nsd.qddt.domain.controlconstruct.pojo.SequenceKind

/**
 * @author Stig Norland
 */
class ConstructSequenceJsonView(construct: Sequence?) : ConstructJsonView(construct) {
    val sequence: List<ElementRefEmbedded<ControlConstruct?>?>?
    val description: String?
    val sequenceKind: SequenceKind?

    init {
        sequenceKind = construct.getSequenceKind()
        sequence = construct!!.sequence
        description = construct.description
    }
}
