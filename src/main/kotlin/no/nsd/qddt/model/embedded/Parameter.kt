package no.nsd.qddt.model.embedded

import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.enums.ParameterKind
import org.hibernate.envers.Audited
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

/**
 * @author Stig Norland
 */

@Audited
@Embeddable
class Parameter(
    var id: UUID? = UUID.randomUUID(),
    var name: String? = null,
    var referencedId: UUID? = null,
    @Enumerated(EnumType.STRING)
    var parameterKind: ParameterKind? = null
) : Comparable<Parameter>, Serializable {


    // constructor() {}
    // constructor(name: String?) {
    //     this.name = name
    // }

    // constructor(name: String?, parameterKind: String?) {
    //     id = UUID.randomUUID()
    //     this.name = name
    //     this.parameterKind = parameterKind
    // }


    fun toDDIXml(entity: AbstractEntityAudit, tabs: String?): String {
        return String.format(PARAM_FORMAT, entity.agency!!.name + ":" + entity.version.toDDIXml(), name, tabs)
    }

    override fun compareTo(other: Parameter): Int {
        val i = parameterKind!!.compareTo(other.parameterKind!!)
        return if (i != 0) i else name!!.compareTo(other.name!!)
    }

    companion object {
        private const val PARAM_FORMAT =
            "%3\$s<r:OutParameter isIdentifiable=\"true\" scopeOfUniqueness=\"Maintainable\" isArray=\"false\">\n" +
                    "%3\$s\t<r:URN>urn:ddi:%1\$s</r:URN>\n" +
                    "%3\$s\t<r:Alias>%2\$s</r:Alias>\n" +
                    "%3\$s</r:OutParameter>\n"
    }
}
