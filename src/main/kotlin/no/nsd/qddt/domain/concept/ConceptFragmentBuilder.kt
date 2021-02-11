package no.nsd.qddt.domain.concept

import no.nsd.qddt.classes.AbstractEntityAudit
import no.nsd.qddt.classes.elementref.ElementKind
import no.nsd.qddt.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.classes.xml.XmlDDIFragmentBuilder
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class ConceptFragmentBuilder(concept: Concept) : XmlDDIFragmentBuilder<Concept>(concept) {
    private val xmlConcept = """%1${"$"}s			<c:ConceptName>
				<r:String xml:lang="%5${"$"}s">%2${"$"}s</r:String>
			</c:ConceptName>
			<r:Description>
				<r:Content xml:lang="%5${"$"}s" isPlainText="false"><![CDATA[%3${"$"}s]]></r:Content>
			</r:Description>
%4${"$"}s		</c:Concept>
"""
    private val children:  MutableList<AbstractXmlBuilder> =  concept.children.stream()
        .filter { it != null }
        .map { it.xmlBuilder }
        .collect(Collectors.toList())

    private val questions:  MutableList<AbstractXmlBuilder> = concept.conceptQuestionItems.stream()
        .filter { it.element != null }
        .map { it.element!!.xmlBuilder }
        .collect(Collectors.toList())

    override fun <S : AbstractEntityAudit> getXmlHeader(instance: S): String {
        val prefix: String = ElementKind.getEnum(instance::class.simpleName).ddiPreFix
        val child = if ((instance as Concept).children.isEmpty()) "" else " isCharacteristic =\"true\""
        return String.format(
            xmlHeader, prefix,
            instance.javaClass.simpleName,
            getInstanceDate(instance),
            child,
            "\t\t\t" + getXmlURN(instance) + getXmlUserId(instance) + getXmlRationale(instance) + getXmlBasedOn(instance)
        )
    }

    override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {
        super.addXmlFragments(fragments)
        for (question in questions) {
            question.addXmlFragments(fragments)
        }
        for (child in children) {
            child.addXmlFragments(fragments)
        }
    }

    override val xmlFragment: String
        get() = java.lang.String.format(xmlConcept,
            getXmlHeader(entity),
            entity.name,
            entity.description,
            children.stream()
                .map { c: AbstractXmlBuilder -> c.getXmlEntityRef(3) }
                .collect(Collectors.joining()),
            entity.xmlLang)


}
