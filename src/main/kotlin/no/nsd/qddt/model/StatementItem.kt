package no.nsd.qddt.model

import no.nsd.qddt.model.builder.ControlConstructFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import javax.persistence.Cacheable
import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

/**
 * @author Stig Norland
 */

@Cacheable
@Entity
@Audited
@DiscriminatorValue("STATEMENT_CONSTRUCT")
data class StatementItem(
    @Column(name = "description")
    var statement: String? = null
) : ControlConstruct() {


    override fun xmlBuilder(): AbstractXmlBuilder {
        return ControlConstructFragmentBuilder(this)
    }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as StatementItem

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name  , modified = $modified , classKind = $classKind )"
    }

}
