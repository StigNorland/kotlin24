package no.nsd.qddt.domain.controlconstruct.json

import no.nsd.qddt.domain.classes.elementref.ElementRefEmbedded
import no.nsd.qddt.domain.controlconstruct.pojo.ControlConstruct
import no.nsd.qddt.domain.controlconstruct.pojo.Sequence
import no.nsd.qddt.domain.controlconstruct.pojo.SequenceKind
import no.nsd.qddt.domain.universe.Universe
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class ConstructSequenceJson(construct: Sequence?) : ConstructJson(construct) {
    val sequence: List<ElementRefEmbedded<ControlConstruct?>?>?
    val description: String?
    val sequenceKind: SequenceKind?
    val universe: String

    companion object {
        /**
         *
         */
        private const val serialVersionUID = -7704864346435586898L
    }

    init {
        sequenceKind = construct.getSequenceKind()
        sequence = construct!!.sequence
        description = construct.description
        universe = construct.universe.stream().map { obj: Universe? -> obj!!.description }
            .collect(Collectors.joining("/ "))
    }
}
