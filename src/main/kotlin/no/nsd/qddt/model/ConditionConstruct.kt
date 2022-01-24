package no.nsd.qddt.model

import no.nsd.qddt.model.builder.ControlConstructFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.embedded.ElementRefEmbedded
import no.nsd.qddt.model.enums.ConditionKind
import no.nsd.qddt.model.interfaces.IConditionNode
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Audited
@DiscriminatorValue("CONDITION_CONSTRUCT")
data class ConditionConstruct(
    @Column(name = "description")
    override var condition: String? = null
) : ControlConstruct(), IConditionNode {


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

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ConditionConstruct

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , modifiedById = $modifiedById , modified = $modified , classKind = $classKind )"
    }
}
