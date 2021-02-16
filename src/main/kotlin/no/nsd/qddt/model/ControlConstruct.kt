package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.model.builder.ControlConstructFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import org.hibernate.envers.Audited
import java.util.ArrayList
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
    private var otherMaterials: List<OtherMaterial>? = mutableListOf()

    @Transient
    @JsonSerialize
    private var parameterIn: Set<Parameter> = mutableSetOf()

    @Transient
    @JsonSerialize
    private var parameterOut: Set<Parameter> = mutableListOf()

    @PostLoad
    private fun setDefault() {
        setClassKind(controlConstructKind)
    }

 

    open fun getParameterIn(): Set<Parameter<*>>? {
        return parameterIn
    }

    fun setParameterIn(parameterIn: Set<Parameter<*>>) {
        this.parameterIn = parameterIn
    }

    open fun getParameterOut(): Set<Parameter<*>>? {
        return parameterOut
    }

    fun setParameterOut(parameterOut: Set<Parameter<*>>) {
        this.parameterOut = parameterOut
    }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        TODO("Not yet implemented")
    }

    protected fun beforeUpdate() {}
    protected fun beforeInsert() {}



    open val xmlBuilder: AbstractXmlBuilder?
        get() = ControlConstructFragmentBuilder<ControlConstruct>(this)
    override var name: String
        get() = TODO("Not yet implemented")
        set(value) {}
}
