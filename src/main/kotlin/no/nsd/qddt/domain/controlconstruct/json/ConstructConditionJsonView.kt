package no.nsd.qddt.domain.controlconstruct.json

import no.nsd.qddt.domain.controlconstruct.pojo.ConditionConstruct
import java.util.*

/**
 * @author Stig Norland
 */
class ConstructConditionJsonView(construct: ConditionConstruct?) : ConstructJsonView(construct) {
    var condition: String?
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as ConstructConditionJsonView
        return if (condition != null) condition == that.condition else that.condition == null
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + if (condition != null) condition.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return StringJoiner(", ", ConstructConditionJsonView::class.java.simpleName + "[", "]")
            .add("condition='$condition'")
            .add("name='$name'")
            .add("id=$id")
            .add("classKind='$classKind'")
            .toString()
    }

    init {
        condition = construct.getCondition()
    }
}
