package no.nsd.qddt.domain.controlconstruct.pojo

import no.nsd.qddt.domain.classes.interfaces.IConditionNode
import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import java.util.*
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


    override fun toString(): String {
        return ("{\"ConditionConstruct\":"
                + super.toString()
                + ", \"condition\":\"" + condition + "\""
                + ", \"conditionKind\":\"" + conditionKind + "\""
                + "}")
    }

    override val xmlBuilder: AbstractXmlBuilder
        get() = ControlConstructFragmentBuilder(this)
}
