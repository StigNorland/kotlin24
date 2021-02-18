package no.nsd.qddt.model.builder

import no.nsd.qddt.model.Publication
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.model.enums.ElementKind
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
class PublicationFragmentBuilder(entity: Publication) : XmlDDIFragmentBuilder<Publication>(entity) {
    private val xmlPublication =
"""%1${"$"}s			<d:PublicationName>
				<r:String>%2${"$"}s</r:String>
			</d:PublicationName>
%3${"$"}s%4${"$"}s"""

    //    r:ConceptReference/r:URN"/>
    //    r:ConceptReference/r:TypeOfObject" defaultValue="Concept" fixedValue="true"/>
    protected var children: MutableList<AbstractXmlBuilder> = mutableListOf()

    override val xmlFragment: String
        get() {
            if (children.size == 0) addChildren()
            return String.format(
                xmlPublication,
                getXmlHeader(entity),
                entity.name,
                children.stream()
                    .map { it.getXmlEntityRef(3) }
                    .collect(Collectors.joining()),
                getXmlFooter(entity)
            )
        }

    override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {
        super.addXmlFragments(fragments)
        if (children.size == 0) addChildren()
        children.stream()
            .forEach { it.addXmlFragments(fragments) }
    }

    private fun addChildren() {
        entity.publicationElements
            .filter { it.element != null }
            .map { it.element!!.xmlBuilder }
            .let { children = it as MutableList<AbstractXmlBuilder> }
    }
}
