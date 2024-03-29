package no.nsd.qddt.model.builder

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.model.enums.CategoryKind

/**
 * @author Stig Norland
 */
open class CategoryFragmentBuilder(category: Category) : XmlDDIFragmentBuilder<Category>(category) {
    private val xmlCategory =
"""
%1${"$"}s<l:CategoryName>
%1${"$"}s   <r:String %2${"$"}s>%3${"$"}s</r:String>
%1${"$"}s</l:CategoryName>
%1${"$"}s<r:Label>
%1${"$"}s	<r:Content %2${"$"}s>%4${"$"}s</r:Content>
%1${"$"}s</r:Label>
%5${"$"}s
"""

    //    d:ScaleDomainReference/r:TypeOfObject" defaultValue="ManagedScaleRepresentation" />
    //    d:TextDomainReference/r:TypeOfObject" defaultValue="ManagedTextRepresentation" />
    //    d:NumericDomainReference/r:TypeOfObject" defaultValue="ManagedNumericRepresentation"/>
    //    d:DateTimeDomainReference/r:TypeOfObject" defaultValue="ManagedDateTimeRepresentation" />
    private val xmlDomainReference =
"""
%3${"$"}s<d:%1${"$"}sDomainReference isExternal="false"  isReference="true" lateBound="false">
%3${"$"}s	%2${"$"}s%3${"$"}s	<r:TypeOfObject>Managed%1${"$"}sRepresentation</r:TypeOfObject>
%3${"$"}s</d:%1${"$"}sDomainReference>
"""
    private val xmlCodeDomain =
"""
%2${"$"}s<d:CodeDomain>
%2${"$"}s	<r:CodeListReference isExternal="false"  isReference="true" lateBound="false">
%2${"$"}s		%1${"$"}s%2${"$"}s		<r:TypeOfObject>CodeList</r:TypeOfObject>
%2${"$"}s	</r:CodeListReference>
%2${"$"}s</d:CodeDomain>
"""
    override val xmlFragment: String
        get() = String.format(
            xmlCategory,
            getXmlHeader(entity),
            getXmlLang(entity),
            entity.name,
            entity.label,
            getXmlFooter(entity)
        )

    override fun getXmlEntityRef(depth: Int): String {
        return when (entity.categoryKind) {
            CategoryKind.CATEGORY ->
                super.getXmlEntityRef(depth)
            CategoryKind.LIST ->
                String.format(xmlCodeDomain, getXmlURN(entity), getTabs(depth))
            else ->
                String.format(xmlDomainReference, entity.categoryKind.name, getXmlURN(entity), getTabs(depth))
        }
    }
}
