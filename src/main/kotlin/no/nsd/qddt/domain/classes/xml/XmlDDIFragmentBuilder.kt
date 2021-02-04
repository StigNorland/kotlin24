package no.nsd.qddt.domain.classes.xml

import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.domain.AbstractEntityAudit.getAgency
import no.nsd.qddt.domain.AbstractEntityAudit.getVersion
import no.nsd.qddt.domain.classes.elementref.*
import java.util.*

/**
 * @author Stig Norland
 */
abstract class XmlDDIFragmentBuilder<T : AbstractEntityAudit?>(protected val entity: T) : AbstractXmlBuilder() {
    protected val xmlRef = """
         %3${"$"}s<r:%1${"$"}sReference>
         %3${"$"}s	%2${"$"}s%3${"$"}s	<r:TypeOfObject>%1${"$"}s</r:TypeOfObject>
         %3${"$"}s</r:%1${"$"}sReference>
         
         """.trimIndent()

    override fun addXmlFragments(fragments: Map<ElementKind?, MutableMap<String?, String>>) {
        var kind: ElementKind? = null
        kind = try {
            ElementKind.Companion.getEnum(entity.javaClass.getSimpleName())
        } catch (ex: Exception) {
            ElementKind.Companion.getEnum(entity!!.classKind)
        } finally {
            fragments[kind]!!.putIfAbsent(urnId, xmlFragment)
        }
    }

    override fun getXmlEntityRef(depth: Int): String? {
        return String.format(
            xmlRef,
            entity.javaClass.getSimpleName(),
            getXmlURN(entity),
            java.lang.String.join("", Collections.nCopies(depth, "\t"))
        )
    }

    val urnId: String
        get() = java.lang.String.format(
            "%1\$s:%2\$s:%3\$s",
            entity!!.getAgency()!!.name,
            entity.id,
            entity.getVersion().toDDIXml()
        )
}
