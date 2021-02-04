package no.nsd.qddt.domain.controlconstruct.pojo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.domain.classes.pdf.PdfReport
import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.domain.instrument.pojo.Parameter
import no.nsd.qddt.domain.othermaterial.OtherMaterial
import no.nsd.qddt.domain.questionitem.QuestionItem
import org.hibernate.envers.Audited
import java.util.*
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
open class ControlConstruct : AbstractEntityAudit() {
    var label: String? = null

    @JsonIgnore
    @Column(name = "CONTROL_CONSTRUCT_KIND", insertable = false, updatable = false)
    private val controlConstructKind: String? = null

    @OrderColumn(name = "owner_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "CONTROL_CONSTRUCT_OTHER_MATERIAL",
        joinColumns = [JoinColumn(name = "owner_id", referencedColumnName = "id")]
    )
    var otherMaterials: List<OtherMaterial?>? = ArrayList()

    @Transient
    @JsonSerialize
    open var parameterIn: Set<Parameter?> = HashSet(0)

    @Transient
    @JsonSerialize
    open var parameterOut: Set<Parameter?> = HashSet(0)
    @PostLoad
    private fun setDefault() {
        classKind = controlConstructKind
    }

    override fun fillDoc(pdfReport: PdfReport?, counter: String?) {}
    override fun beforeUpdate() {}
    override fun beforeInsert() {}
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is ControlConstruct) return false
        if (!super.equals(o)) return false
        val that = o
        if (if (label != null) label != that.label else that.label != null) return false
        return if (otherMaterials != null) otherMaterials == that.otherMaterials else that.otherMaterials == null
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + if (label != null) label.hashCode() else 0
        result = 31 * result + if (otherMaterials != null) otherMaterials.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return ("{\"ControlConstruct\":"
                + super.toString()
                + ", \"label\":\"" + label + "}")
    }

    override val xmlBuilder: AbstractXmlBuilder
        get() = ControlConstructFragmentBuilder(this)
}
