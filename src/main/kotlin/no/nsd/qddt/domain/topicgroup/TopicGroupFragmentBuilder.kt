package no.nsd.qddt.domain.topicgroup

import no.nsd.qddt.domain.classes.elementref.ElementKind
import no.nsd.qddt.domain.classes.elementref.ElementRefEmbedded
import no.nsd.qddt.domain.classes.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.domain.concept.ConceptFragmentBuilder
import no.nsd.qddt.domain.questionitem.QuestionItem
import no.nsd.qddt.domain.questionitem.QuestionItemFragmentBuilder
import java.util.*
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class TopicGroupFragmentBuilder(topicGroup: TopicGroup) : XmlDDIFragmentBuilder<TopicGroup?>(topicGroup) {
    private val xmlTopic = """		<c:ConceptGroup isOrdered= "false" isAdministrativeOnly="true">
			%1${"$"}s%2${"$"}s%3${"$"}s			<c:ConceptGroupName>
				<r:String xml:lang="%6${"$"}s">%4${"$"}s</r:String>
			</c:ConceptGroupName>
			<r:Description>
				<r:Content xml:lang="%6${"$"}s" isPlainText="false">%5${"$"}s</r:Content>
			</r:Description>
%7${"$"}s		</c:ConceptGroup>
"""
    private val children: List<ConceptFragmentBuilder>
    private val questions: List<QuestionItemFragmentBuilder>
    fun addXmlFragments(fragments: Map<ElementKind?, Map<String?, String>>) {
        super.addXmlFragments(fragments)
        //        fragments.putIfAbsent( getUrnId(), getXmlFragment() );
        for (child in children) {
            child.addXmlFragments(fragments)
        }
        for (question in questions) {
            question.addXmlFragments(fragments)
        }
    }

    override fun getXmlEntityRef(depth: Int): String? {
        return kotlin.String.format(
            xmlRef,
            "ConceptGroup",
            getXmlURN<TopicGroup>(entity),
            java.lang.String.join("", Collections.nCopies(depth, "\t"))
        )
    }

    val xmlFragment: String
        get() = String.format(
            xmlTopic,
            getXmlURN<TopicGroup>(entity),
            getXmlRationale<TopicGroup>(entity),
            getXmlBasedOn<TopicGroup>(entity),
            entity.name,
            entity.description,
            entity.xmlLang,
            children.stream()
                .map(Function<ConceptFragmentBuilder, Any> { c: ConceptFragmentBuilder -> c.getXmlEntityRef(3) })
                .collect(Collectors.joining())
        )

    init {
        children = topicGroup.getConcepts().stream()
            .map(Function<Concept, Any?> { c: Concept -> c.getXmlBuilder() as ConceptFragmentBuilder })
            .collect(Collectors.toList<Any>())
        questions = topicGroup.topicQuestionItems.stream()
            .filter(Predicate { f: ElementRefEmbedded<QuestionItem?> -> f.getElement() != null })
            .map(Function<ElementRefEmbedded<QuestionItem?>, QuestionItemFragmentBuilder?> { cqi: ElementRefEmbedded<QuestionItem?> -> cqi.getElement().xmlBuilder as QuestionItemFragmentBuilder })
            .collect(Collectors.toList())
    }
}
