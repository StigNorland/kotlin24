package no.nsd.qddt.domain.controlconstruct.pojo

import no.nsd.qddt.domain.classes.elementref.ElementKind
import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.domain.classes.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.domain.othermaterial.OtherMaterial
import java.util.*
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
open class ControlConstructFragmentBuilder<T : ControlConstruct?>(entity: T) : XmlDDIFragmentBuilder<T>(entity) {
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
    open fun addXmlFragments(fragments: Map<ElementKind?, Map<String?, String>>) {
        super.addXmlFragments(fragments)
        //        children.stream().forEach( c -> c.addXmlFragments( fragments ) );
    }

    //            entity.getOutParameter().stream().map( p -> p.toDDIXml( entity,"\t\t\t" ) ).collect( Collectors.joining())+
    override val xmlFragment: String
        get() = String.format(xmlConstruct,
            getXmlHeader(entity),
            getXmlLang(entity),
            entity!!.name,
            entity.label,
            entity.otherMaterials.stream().map { o: OtherMaterial? -> o!!.toDDIXml(entity, "\t\t\t") }
                .collect(Collectors.joining()),
            children.stream().map { c: AbstractXmlBuilder -> c.getXmlEntityRef(3) }.collect(Collectors.joining()),
            getXmlFooter(entity)
        )

    init {
        children = LinkedList()
    }
}
