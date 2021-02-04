package no.nsd.qddt.domain.controlconstruct.factory

import no.nsd.qddt.domain.IEntityFactory
import no.nsd.qddt.domain.classes.elementref.ElementRefEmbedded
import no.nsd.qddt.domain.controlconstruct.pojo.ControlConstruct
import no.nsd.qddt.domain.controlconstruct.pojo.Sequence
import no.nsd.qddt.domain.othermaterial.OtherMaterial
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class FactorySequenceConstruct : IEntityFactory<Sequence?> {
    override fun create(): Sequence {
        return Sequence()
    }

    override fun copyBody(source: Sequence, dest: Sequence): Sequence {
        dest.label = source.label
        dest.otherMaterials = source.otherMaterials.stream()
            .map { m: OtherMaterial -> m.clone() }
            .collect(Collectors.toList())
        dest.description = source.description
        dest.sequenceKind = source.sequenceKind
        dest.sequence = source.sequence!!.stream()
            .map { obj: ElementRefEmbedded<ControlConstruct?>? -> obj!!.clone() }
            .collect(Collectors.toList())
        return dest
    }
}
