package no.nsd.qddt.domain.controlconstruct.json

import no.nsd.qddt.domain.controlconstruct.pojo.StatementItem

/**
 * @author Stig Norland
 */
class ConstructStatementJson(construct: StatementItem?) : ConstructJson(construct) {
    val statement: String?
    override fun equals(o: Any): Boolean {
        if (this === o) return true
        if (o !is ConstructStatementJson) return false
        if (!super.equals(o)) return false
        val that = o
        return if (statement != null) statement == that.statement else that.statement == null
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (statement?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return ("{\"ConstructStatementJson\":"
                + super.toString()
                + ", \"statement\":\"" + statement + "\""
                + "}")
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 1580182686378733470L
    }

    init {
        statement = construct.getStatement()
    }
}
