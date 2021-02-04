package no.nsd.qddt.domain.controlconstruct.json

import no.nsd.qddt.domain.controlconstruct.pojo.ConditionConstruct

/**
 * @author Stig Norland
 */
class ConstructConditionJson(construct: ConditionConstruct?) : ConstructJson(construct) {
    var condition: String?
    override fun equals(o: Any): Boolean {
        if (this === o) return true
        if (o !is ConstructConditionJson) return false
        val that = o
        return if (condition != null) condition == that.condition else that.condition == null
    }

    override fun hashCode(): Int {
        return if (condition != null) condition.hashCode() else 0
    }

    override fun toString(): String {
        return ("{\"ConstructConditionJson\":"
                + super.toString()
                + ", \"condition\":\"" + condition + "\""
                + "}")
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 8040983806216492534L
    }

    init {
        condition = construct.getCondition()
    }
}
