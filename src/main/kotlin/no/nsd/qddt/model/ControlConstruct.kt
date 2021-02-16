package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.builder.ControlConstructFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
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
class ControlConstruct : AbstractEntityAudit() {

    var label: String? = null
    override lateinit var name: String
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


    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        TODO("Not yet implemented")
    }

    override fun beforeUpdate() {
        TODO("Not yet implemented")
    }

    override fun beforeInsert() {
        TODO("Not yet implemented")
    }


    override val xmlBuilder: AbstractXmlBuilder
        get() = ControlConstructFragmentBuilder(this)

}
