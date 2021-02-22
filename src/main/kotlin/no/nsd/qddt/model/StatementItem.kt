package no.nsd.qddt.model

import no.nsd.qddt.model.builder.ControlConstructFragmentBuilder
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

    override fun xmlBuilder(): AbstractXmlBuilder {
        return ControlConstructFragmentBuilder(this)
    }
}
