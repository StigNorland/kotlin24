package no.nsd.qddt.domain.controlconstruct.factory

import no.nsd.qddt.domain.IEntityFactory
import no.nsd.qddt.domain.controlconstruct.pojo.StatementItem
import no.nsd.qddt.domain.othermaterial.OtherMaterial
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class FactoryStatementConstruct : IEntityFactory<StatementItem?> {
    override fun create(): StatementItem {
        return StatementItem()
    }

    override fun copyBody(source: StatementItem, dest: StatementItem): StatementItem {
        dest.label = source.label
        dest.statement = source.statement
        dest.otherMaterials = source.otherMaterials.stream()
            .map { m: OtherMaterial -> m.clone() }
            .collect(Collectors.toList())
        return dest
    }
}
