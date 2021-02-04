package no.nsd.qddt.domain.controlconstruct.factory

import no.nsd.qddt.domain.IEntityFactory
import no.nsd.qddt.domain.controlconstruct.pojo.QuestionConstruct
import no.nsd.qddt.domain.othermaterial.OtherMaterial
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class FactoryQuestionConstruct : IEntityFactory<QuestionConstruct?> {
    override fun create(): QuestionConstruct {
        return QuestionConstruct()
    }

    override fun copyBody(source: QuestionConstruct, dest: QuestionConstruct): QuestionConstruct {
        dest.label = source.label
        dest.questionItemRef = source.questionItemRef
        dest.otherMaterials = source.otherMaterials.stream()
            .map { m: OtherMaterial -> m.clone() }.collect(Collectors.toList())
        dest.universe = source.universe
        dest.controlConstructInstructions = source.controlConstructInstructions
        return dest
    }
}
