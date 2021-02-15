package no.nsd.qddt.model

import no.nsd.qddt.domain.classes.elementref.ElementRefEmbedded
import no.nsd.qddt.model.classes.ConditionKind
import java.util.ArrayList
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType

/**
 * @author Stig Norland
 */
@Entity
@Audited
@DiscriminatorValue("CONDITION_CONSTRUCT")
class ConditionConstruct : ControlConstruct(), IConditionNode {
    @Column(name = "description")
    var condition: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "CONTROL_CONSTRUCT_SUPER_KIND")
    var conditionKind: ConditionKind? = null

    @OrderColumn(name = "sequence_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "CONTROL_CONSTRUCT_SEQUENCE",
        joinColumns = [JoinColumn(name = "sequence_id", referencedColumnName = "id")]
    )
    var sequence: List<ElementRefEmbedded<ControlConstruct>> = ArrayList<ElementRefEmbedded<ControlConstruct>>(0)
    fun getConditionKind(): ConditionKind? {
        return conditionKind
    }

    fun setConditionKind(conditionKind: ConditionKind?) {
        this.conditionKind = conditionKind
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is ConditionConstruct) return false
        if (!super.equals(o)) return false
        val that = o
        return if (condition != that.condition) false else conditionKind === that.conditionKind
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + if (condition != null) condition.hashCode() else 0
        result = 31 * result + if (conditionKind != null) conditionKind.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return ("{\"ConditionConstruct\":"
                + super.toString()
                ) + ", \"condition\":\"" + condition.toString() + "\"" + ", \"conditionKind\":\"" + conditionKind.toString() + "\"" + "}"
    }

    val xmlBuilder: AbstractXmlBuilder
        get() = ControlConstructFragmentBuilder(this)
}
