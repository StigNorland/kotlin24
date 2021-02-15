package no.nsd.qddt.model.builder

import no.nsd.qddt.model.ControlConstruct
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.model.classes.elementref.ElementKind
import java.util.*
import java.util.function.Function

/**
 * @author Stig Norland
 */
class ControlConstructFragmentBuilder<T : ControlConstruct?>(entity: T) : XmlDDIFragmentBuilder<T>(entity) {
    private val xmlConstruct = """%1${"$"}s			<d:ConstructName>
				<r:String>%3${"$"}s</r:String>
			</d:ConstructName>
			<r:Label>
				<r:Content %2${"$"}s>%4${"$"}s</r:Content>
			</r:Label>
%5${"$"}s%6${"$"}s%7${"$"}s"""

    //    r:ConceptReference/r:URN"/>
    //    r:ConceptReference/r:TypeOfObject" defaultValue="Concept" fixedValue="true"/>
    protected var children: List<AbstractXmlBuilder>
    fun addXmlFragments(fragments: Map<ElementKind?, Map<String?, String?>?>?) {
        super.addXmlFragments(fragments)
        //        children.stream().forEach( c -> c.addXmlFragments( fragments ) );
    }

    //            entity.getOutParameter().stream().map( p -> p.toDDIXml( entity,"\t\t\t" ) ).collect( Collectors.joining())+
    val xmlFragment: String
        get() = java.lang.String.format(
            xmlConstruct,
            getXmlHeader(entity),
            getXmlLang(entity),
            entity.getName(),
            entity.getLabel(),
            entity.getOtherMaterials().stream().map { o -> o.toDDIXml(entity, "\t\t\t") }.collect(Collectors.joining()),
            children.stream().map(Function<AbstractXmlBuilder, Any> { c: AbstractXmlBuilder -> c.getXmlEntityRef(3) })
                .collect(Collectors.joining()),
            getXmlFooter(entity)
        )

    init {
        children = LinkedList<AbstractXmlBuilder>()
    }
}
