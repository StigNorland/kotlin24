package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.builder.ControlConstructFragmentBuilder
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.embedded.Parameter
import org.hibernate.envers.Audited
import javax.persistence.*

/**
 * Instrument is the significant relation.
 * Instrument will be asked for all [QuestionItem] instances it has and the
 * metadata in this class will be used as visual condition for each [QuestionItem].
 *
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */
@Audited
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CONTROL_CONSTRUCT_KIND")
@Table(name = "CONTROL_CONSTRUCT")
abstract class ControlConstruct(
    var label: String = "",
    override var name: String = ""
) : AbstractEntityAudit() {



    @Transient
    @JsonSerialize
    override var classKind: String = ""
        get() =  controlConstructKind?:field

    @JsonIgnore
    @Column(name = "CONTROL_CONSTRUCT_KIND", insertable = false, updatable = false)
    protected val controlConstructKind: String? = null

    @OrderColumn(name = "owner_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "CONTROL_CONSTRUCT_OTHER_MATERIAL",
        joinColumns = [JoinColumn(name = "owner_id", referencedColumnName = "id")]
    )
    var otherMaterials: MutableList<OtherMaterial> = mutableListOf()

    @Transient
    @JsonSerialize
    var parameterIn: Set<Parameter> = mutableSetOf()

    @Transient
    @JsonSerialize
    var parameterOut: Set<Parameter> = mutableSetOf()


    override fun xmlBuilder(): AbstractXmlBuilder {
        return ControlConstructFragmentBuilder(this)
    }

}
