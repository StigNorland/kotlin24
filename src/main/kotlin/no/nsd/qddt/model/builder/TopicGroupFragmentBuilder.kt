package no.nsd.qddt.model.builder

import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.model.enums.ElementKind
import java.util.*
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class TopicGroupFragmentBuilder(topicGroup: TopicGroup) : XmlDDIFragmentBuilder<TopicGroup>(topicGroup) {
    private val xmlTopic =
"""		<c:ConceptGroup isOrdered= "false" isAdministrativeOnly="true">
			%1${"$"}s%2${"$"}s%3${"$"}s			<c:ConceptGroupName>
				<r:String xml:lang="%6${"$"}s">%4${"$"}s</r:String>
			</c:ConceptGroupName>
			<r:Description>
				<r:Content xml:lang="%6${"$"}s" isPlainText="false">%5${"$"}s</r:Content>
			</r:Description>
%7${"$"}s		</c:ConceptGroup>
"""

    // private val childrenBuilders:  MutableList<AbstractXmlBuilder> = topicGroup.concepts.stream()
    //     .filter { it != null }
    //     .map { it.xmlBuilder() }
    //     .collect(Collectors.toList())

    private val questions:  MutableList<AbstractXmlBuilder> = topicGroup.questionItems.stream()
        .filter { it.element != null }
        .map { it.element!!.xmlBuilder() }
        .collect(Collectors.toList())

    // private val questions: List<QuestionItemFragmentBuilder>

    override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {
        super.addXmlFragments(fragments)
        //        fragments.putIfAbsent( getUrnId(), getXmlFragment() );
        // for (child in childrenBuilders) {
        //     child.addXmlFragments(fragments)
        // }
        for (question in questions) {
            question.addXmlFragments(fragments)
        }
    }

    override fun getXmlEntityRef(depth: Int): String {
        return String.format(
            xmlRef,
            "ConceptGroup",
            getXmlURN(entity),
            java.lang.String.join("", Collections.nCopies(depth, "\t"))
        )
    }

    override val xmlFragment: String
        get() {
            return String.format(
                xmlTopic,
                getXmlURN(entity),
                getXmlRationale(entity),
                getXmlBasedOn(entity),
                entity.name,
                entity.description,
                entity.xmlLang,
                ""
                // childrenBuilders.stream().map { it.getXmlEntityRef(3) }.collect(Collectors.joining())
            )    
        }
}
