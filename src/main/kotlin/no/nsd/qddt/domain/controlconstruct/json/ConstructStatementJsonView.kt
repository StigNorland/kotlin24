package no.nsd.qddt.domain.controlconstruct.json

import no.nsd.qddt.domain.controlconstruct.pojo.StatementItem

/**
 * @author Stig Norland
 */
class ConstructStatementJsonView(construct: StatementItem?) : ConstructJsonView(construct) {
    val statement: String?
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as ConstructStatementJsonView
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

    init {
        statement = construct.getStatement()
    }
}
