package no.nsd.qddt.model.builder

import no.nsd.qddt.model.ControlConstruct
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
open class ControlConstructFragmentBuilder<T : ControlConstruct>(entity: T) : XmlDDIFragmentBuilder<T>(entity) {
    private val xmlConstruct = """%1${"$"}s			<d:ConstructName>
				<r:String>%3${"$"}s</r:String>
			</d:ConstructName>
			<r:Label>
				<r:Content %2${"$"}s>%4${"$"}s</r:Content>
			</r:Label>
%5${"$"}s%6${"$"}s%7${"$"}s"""

    //    r:ConceptReference/r:URN"/>
    //    r:ConceptReference/r:TypeOfObject" defaultValue="Concept" fixedValue="true"/>
    protected var children: MutableList<AbstractXmlBuilder> = mutableListOf()

//    override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {
//        super.addXmlFragments(fragments)
//        //        children.stream().forEach( c -> c.addXmlFragments( fragments ) );
//    }


    //            entity.getOutParameter().stream().map( p -> p.toDDIXml( entity,"\t\t\t" ) ).collect( Collectors.joining())+
    override val xmlFragment: String
        get() = String.format(
            xmlConstruct,
            getXmlHeader(entity),
            getXmlLang(entity),
            entity.name,
            entity.label,
            entity.otherMaterials.stream().map { o -> o.toDDIXml(entity, "\t\t\t") }.collect(Collectors.joining()),
            children.stream().map { c: AbstractXmlBuilder -> c.getXmlEntityRef(3) }
                .collect(Collectors.joining()),
            getXmlFooter(entity)
        )

}
