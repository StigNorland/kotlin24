package no.nsd.qddt.model.builder.xml

import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.enums.ElementKind
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap

/**
 * @author Stig Norland
 */
open class XmlDDIFragmentAssembler<T : AbstractEntityAudit>(private val rootElement: T) {
    private val XMLDEF = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    private val xmlFragHeader = """
<ddi:FragmentInstance 
	xmlns:c="ddi:conceptualcomponent:3_2" 
	xmlns:d="ddi:datacollection:3_2" 
	xmlns:ddi="ddi:instance:3_2" 
	xmlns:g="ddi:group:3_2" 
	xmlns:l="ddi:logicalproduct:3_2" 
	xmlns:r="ddi:reusable:3_2" 
	xmlns:s="ddi:studyunit:3_2" 
	xmlns:xhtml="http://www.w3.org/1999/xhtml" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xsi:schemaLocation="ddi:instance:3_2  https://ddialliance.org/Specification/DDI-Lifecycle/3.2/XMLSchema/instance.xsd">
"""
    private val builder: AbstractXmlBuilder = rootElement.xmlBuilder()!!
    private val orderedFragments: MutableMap<ElementKind, MutableMap<String, String>> = EnumMap(ElementKind::class.java)

    private fun getTopLevelReference(typeOfObject: String): String {
        return """	<ddi:TopLevelReference isExternal="false" externalReferenceDefaultURI="false" isReference="true" lateBound="false" objectLanguage="en-GB">
		${builder.getXmlURN(rootElement)}		<r:TypeOfObject>$typeOfObject</r:TypeOfObject>
	</ddi:TopLevelReference>
"""
    }

    private val footer: String
        get() = "</ddi:FragmentInstance>\n"

    fun compileToXml(): String {
        // rootElement.getClass().getSimpleName().equals( "TopicGroup" )
        val typeofObject = when (rootElement) {
            is TopicGroup -> "ConceptGroup"
            else -> rootElement::class.simpleName?:"WTF"
        }
        val sb = StringBuilder()
        return sb.append(XMLDEF)
            .append(xmlFragHeader)
            .append(getTopLevelReference(typeofObject))
            .append(orderedFragments.entries.stream()
                .sorted()
                .filter { it.value.isNotEmpty() }
                .map {
                    it.value.values.stream()
                        .sorted(Comparator.comparing { s: String -> s.substring(0, 16) })
                        .collect(
                            Collectors.joining(
                                "\t</ddi:Fragment>\n\t<ddi:Fragment>\n",
                                "\t<ddi:Fragment>\n",
                                "\t</ddi:Fragment>\n"
                            )
                        )
                }
                .collect(Collectors.joining()))
            .append(footer)
            .toString()
    }

    init {
        listOf(*ElementKind.values()).forEach {
                kind: ElementKind -> orderedFragments[kind] = HashMap()
        }
        builder.addXmlFragments(orderedFragments)
    }
}
