package no.nsd.qddt.model.classes

import no.nsd.qddt.model.classes.AbstractEntityAudit.agency
import no.nsd.qddt.model.Agency.name
import no.nsd.qddt.model.classes.AbstractEntityAudit.version
import no.nsd.qddt.model.classes.Version.toDDIXml
import org.hibernate.envers.Audited
import javax.persistence.Embeddable
import java.util.UUID
import no.nsd.qddt.model.classes.AbstractEntityAudit
import javax.persistence.Column

/**
 * @author Stig Norland
 */
@Audited
@Embeddable
class Parameter : Comparable<Parameter> {
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
    var name: String? = null
    var referencedId: UUID? = null
    var parameterKind: String? = null

    constructor() {}
    constructor(name: String?) {
        this.name = name
    }

    constructor(name: String?, parameterKind: String?) {
        id = UUID.randomUUID()
        this.name = name
        this.parameterKind = parameterKind
    }


    fun toDDIXml(entity: AbstractEntityAudit, tabs: String?): String {
        return String.format(PARAM_FORMAT, entity.agency.name + ":" + entity.version.toDDIXml(), name, tabs)
    }

    override fun compareTo(parameter: Parameter): Int {
        val i = parameterKind!!.compareTo(parameter.parameterKind!!)
        return if (i != 0) i else name!!.compareTo(parameter.name!!)
    }

    companion object {
        private const val PARAM_FORMAT =
            "%3\$s<r:OutParameter isIdentifiable=\"true\" scopeOfUniqueness=\"Maintainable\" isArray=\"false\">\n" +
                    "%3\$s\t<r:URN>urn:ddi:%1\$s</r:URN>\n" +
                    "%3\$s\t<r:Alias>%2\$s</r:Alias>\n" +
                    "%3\$s</r:OutParameter>\n"
    }
}
