package no.nsd.qddt.model

import no.nsd.qddt.model.builder.ControlConstructFragmentBuilder
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.embedded.ElementRefEmbedded
import no.nsd.qddt.model.enums.ConditionKind
import no.nsd.qddt.model.interfaces.IConditionNode
import org.hibernate.envers.Audited
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Audited
@DiscriminatorValue("CONDITION_CONSTRUCT")
class ConditionConstruct : ControlConstruct(), IConditionNode {
    @Column(name = "description")
    override var condition: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "CONTROL_CONSTRUCT_SUPER_KIND")
    override lateinit var conditionKind: ConditionKind

    @OrderColumn(name = "sequence_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "CONTROL_CONSTRUCT_SEQUENCE",
        joinColumns = [JoinColumn(name = "sequence_id", referencedColumnName = "id")]
    )
    var sequence: MutableList<ElementRefEmbedded<ControlConstruct>> = mutableListOf()


    override fun xmlBuilder(): AbstractXmlBuilder {
        return ControlConstructFragmentBuilder(this)
    }
}
