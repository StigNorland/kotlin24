package no.nsd.qddt.model.builder

import no.nsd.qddt.model.Instrument
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.model.enums.ElementKind
import java.util.stream.Collectors


/**
 * @author Stig Norland
 */
class InstrumentFragmentBuilder(entity: Instrument) : XmlDDIFragmentBuilder<Instrument>(entity) {
    private val xmlInstrument = """%1${"$"}s			<d:ConstructName>
				<r:String>%3${"$"}s</r:String>
			</d:ConstructName>
			<r:Label>
				<r:Content %2${"$"}s>%4${"$"}s</r:Content>
			</r:Label>
%5${"$"}s%6${"$"}s%7${"$"}s"""

    //    r:ConceptReference/r:URN"/>
    //    r:ConceptReference/r:TypeOfObject" defaultValue="Concept" fixedValue="true"/>
    protected var children: MutableList<AbstractXmlBuilder> = mutableListOf()

    override val xmlFragment: String
        get() {
            if (children.size == 0) addChildren()
            return String.format(
                xmlInstrument,
                getXmlHeader(entity),
                getXmlLang(entity),
                entity.name,
                entity.label,
                children.stream()
                    .map{ it.getXmlEntityRef(3) }
                    .collect(Collectors.joining()),
                getXmlFooter(entity)
            )
        }

    private fun addChildren() {
        entity.root?.children?. let{ mutableList -> mutableList
            .filter { it.element != null }
            .map { it.element!!.xmlBuilder() }
            .let { children.addAll(it.toList()) }
        }
    }


    override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {
        super.addXmlFragments(fragments)
        if (children.size == 0)
            addChildren()
        children.stream()
            .forEach { it.addXmlFragments(fragments) }
    }



}
