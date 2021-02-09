package no.nsd.qddt.classes.xml

import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.classes.elementref.*
import java.util.*

/**
 * @author Stig Norland
 */
abstract class XmlDDIFragmentBuilder<T : AbstractEntityAudit>(protected val entity: T) : AbstractXmlBuilder() {
    protected val xmlRef = """
         %3${"$"}s<r:%1${"$"}sReference>
         %3${"$"}s	%2${"$"}s%3${"$"}s	<r:TypeOfObject>%1${"$"}s</r:TypeOfObject>
         %3${"$"}s</r:%1${"$"}sReference>
         
         """.trimIndent()

    override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {
        var kind: ElementKind = 
        try {
            ElementKind.getEnum(entity.classKind)
        } catch (ex: Exception) {
            ElementKind.getEnum(entity::class.simpleName)
        }
        fragments[kind]!!.putIfAbsent(urnId, xmlFragment)
        
    }

    override fun getXmlEntityRef(depth: Int): String {
        return String.format(
            xmlRef,
            entity::class.simpleName,
            getXmlURN(entity),
            java.lang.String.join("", Collections.nCopies(depth, "\t"))
        )
    }

    val urnId: String
        get() = java.lang.String.format(
            "%1\$s:%2\$s:%3\$s",
            entity.agency!!.name,
            entity.id,
            entity.version.toDDIXml()
        )
}
