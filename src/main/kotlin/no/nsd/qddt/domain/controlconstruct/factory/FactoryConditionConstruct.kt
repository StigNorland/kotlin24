package no.nsd.qddt.domain.controlconstruct.factory

import no.nsd.qddt.domain.IEntityFactory
import no.nsd.qddt.domain.controlconstruct.pojo.ConditionConstruct
import no.nsd.qddt.domain.othermaterial.OtherMaterial
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class FactoryConditionConstruct : IEntityFactory<ConditionConstruct?> {
    override fun create(): ConditionConstruct {
        return ConditionConstruct()
    }

    override fun copyBody(source: ConditionConstruct, dest: ConditionConstruct): ConditionConstruct {
        dest.label = source.label
        dest.condition = source.condition
        dest.conditionKind = source.conditionKind
        dest.otherMaterials = source.otherMaterials.stream()
            .map { m: OtherMaterial -> m.clone() }
            .collect(Collectors.toList())
        return dest
    }
}
