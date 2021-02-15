package no.nsd.qddt.model.builder

import no.nsd.qddt.model.Instrument
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.model.classes.elementref.ElementKind
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class InstrumentFragmentBuilder(entity: Instrument?) : XmlDDIFragmentBuilder<Instrument?>(entity) {
    private val xmlInstrument = """%1${"$"}s			<d:ConstructName>
				<r:String>%3${"$"}s</r:String>
			</d:ConstructName>
			<r:Label>
				<r:Content %2${"$"}s>%4${"$"}s</r:Content>
			</r:Label>
%5${"$"}s%6${"$"}s%7${"$"}s"""

    //    r:ConceptReference/r:URN"/>
    //    r:ConceptReference/r:TypeOfObject" defaultValue="Concept" fixedValue="true"/>
    protected var children: List<AbstractXmlBuilder>
    val xmlFragment: String
        get() {
            if (children.size == 0) addChildren()
            return java.lang.String.format(
                xmlInstrument,
                getXmlHeader(entity),
                getXmlLang(entity),
                entity.getName(),
                entity.getLabel(),
                children.stream()
                    .map(Function<AbstractXmlBuilder, Any> { c: AbstractXmlBuilder -> c.getXmlEntityRef(3) })
                    .collect(Collectors.joining()),
                getXmlFooter(entity)
            )
        }

    fun addXmlFragments(fragments: Map<ElementKind?, Map<String?, String?>?>?) {
        super.addXmlFragments(fragments)
        if (children.size == 0) addChildren()
        children.stream()
            .forEach(Consumer<AbstractXmlBuilder> { c: AbstractXmlBuilder -> c.addXmlFragments(fragments) })
    }

    private fun addChildren() {
//        List<AbstractXmlBuilder> collect = entity.getRoot().getChildren().stream()
//        .map( seq -> seq.getElement().getXmlBuilder())
//        .collect( Collectors.toList());
//
//        children.addAll(collect);
    }

    init {
        children = LinkedList<AbstractXmlBuilder>()
    }
}
