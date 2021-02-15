package no.nsd.qddt.model

import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import org.hibernate.envers.Audited
import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

/**
 * @author Stig Norland
 */
@Entity
@Audited
@DiscriminatorValue("STATEMENT_CONSTRUCT")
class StatementItem : ControlConstruct() {
    @Column(name = "description")
    var statement: String? = null
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is StatementItem) return false
        if (!super.equals(o)) return false
        val that = o
        return if (statement != null) statement == that.statement else that.statement == null
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + if (statement != null) statement.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return ("{\"StatementConstruct\":"
                + super.toString()
                + ", \"statement\":\"" + statement + "\""
                + "}")
    }

    override val xmlBuilder: AbstractXmlBuilder
        get() = ControlConstructFragmentBuilder<StatementItem>(this)
}
