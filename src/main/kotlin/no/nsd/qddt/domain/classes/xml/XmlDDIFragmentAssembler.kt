package no.nsd.qddt.domain.classes.xml

import no.nsd.qddt.domain.classes.elementref.ElementKind
import no.nsd.qddt.domain.topicgroup.TopicGroup
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import kotlin.collections.HashMap

/**
 * @author Stig Norland
 */
class XmlDDIFragmentAssembler<T : AbstractEntityAudit?>(private val rootElement: T) {
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
    private val builder: AbstractXmlBuilder
    private val orderedFragments: MutableMap<ElementKind?, MutableMap<String?, String>> = HashMap()
    protected fun getTopLevelReference(typeOfObject: String): String {
        return """	<ddi:TopLevelReference isExternal="false" externalReferenceDefaultURI="false" isReference="true" lateBound="false" objectLanguage="en-GB">
		${builder.getXmlURN(rootElement)}		<r:TypeOfObject>$typeOfObject</r:TypeOfObject>
	</ddi:TopLevelReference>
"""
    }

    protected val footer: String
        protected get() = "</ddi:FragmentInstance>\n"

    fun compileToXml(): String {
        // rootElement.getClass().getSimpleName().equals( "TopicGroup" )
        val typeofObject = if (rootElement is TopicGroup) "ConceptGroup" else rootElement.javaClass.getSimpleName()
        val sb = StringBuilder()
        return sb.append(XMLDEF)
            .append(xmlFragHeader)
            .append(getTopLevelReference(typeofObject))
            .append(orderedFragments.entries.stream()
                .filter { p: Map.Entry<ElementKind?, Map<String?, String>> -> !p.value.isEmpty() }
                .sorted(Comparator.comparing(Function<Map.Entry<ElementKind, Map<String, String>>, ElementKind> { java.util.Map.Entry.key }))
                .map<String> { f: Map.Entry<ElementKind?, Map<String?, String>> ->
                    f.value.values.stream()
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
        builder = rootElement.xmlBuilder
        Arrays.asList(*ElementKind.values())
            .forEach(Consumer { kind: ElementKind? -> orderedFragments[kind] = HashMap() })
        builder.addXmlFragments(orderedFragments)
    }
}
