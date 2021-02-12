package no.nsd.qddt.model.builder

import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.Category
import no.nsd.qddt.model.classes.elementref.ElementKind
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.classes.ResponseKind
import java.util.stream.Collectors
/**
* @author Stig Norland
*/
class ResponseDomainFragmentBuilder(responseDomain: ResponseDomain):XmlDDIFragmentBuilder<ResponseDomain>(responseDomain) {
  // d:StructuredMixedResponseDomain/d:ResponseDomainInMixed"/>
  private val xmlMixedRef = (
    "\t\t\t<d:StructuredMixedResponseDomain>\n" +
    "%1\$s" +
    "\t\t\t</d:StructuredMixedResponseDomain>\n")
  private val xmlCodeDomRef = (
    "%1\$s<d:CodeDomain blankIsMissingValue = \"false\" classificationLevel = \"%2\$s\">\n" +
    "%3\$s" +
    "%4\$s" +
    "%1\$s</d:CodeDomain>\n")
  private val xmlInMixed = (
    "\t\t\t\t<d:ResponseDomainInMixed>\n" +
    "%1\$s" +
    "\t\t\t\t</d:ResponseDomainInMixed>\n")
  // d:ScaleDomainReference/r:TypeOfObject" defaultValue="ManagedScaleRepresentation" fixedValue="true"/>
  // d:CodeDomain/r:CodeListReference/r:TypeOfObject" defaultValue="CodeList" fixedValue="true"/>
  // d:TextDomainReference/r:TypeOfObject" defaultValue="ManagedTextRepresentation" fixedValue="true"/>
  // d:NumericDomainReference/r:TypeOfObject" defaultValue="ManagedNumericRepresentation" fixedValue="true"/>
  // d:DateTimeDomainReference/r:TypeOfObject" defaultValue="ManagedDateTimeRepresentation" fixedValue="true"/>
  //
  // d:StructuredMixedResponseDomain/d:ResponseDomainInMixed"/>
  //
  // d:StructuredMixedResponseDomain/d:ResponseDomainInMixed/d:CodeDomain"/>
  // d:StructuredMixedResponseDomain/d:ResponseDomainInMixed/d:CodeDomain/r:CodeListReference/r:URN"/>
  // d:StructuredMixedResponseDomain/d:ResponseDomainInMixed/d:CodeDomain/r:CodeListReference/r:TypeOfObject" defaultValue="CodeList" fixedValue="true"/>
  // d:StructuredMixedResponseDomain/d:ResponseDomainInMixed/d:ScaleDomainReference/r:TypeOfObject" defaultValue="ManagedScaleRepresentation" fixedValue="true"/>
  // d:StructuredMixedResponseDomain/d:ResponseDomainInMixed/d:TextDomainReference/r:TypeOfObject" defaultValue="ManagedTextRepresentation" fixedValue="true"/>
  // d:StructuredMixedResponseDomain/d:ResponseDomainInMixed/d:NumericDomainReference/r:TypeOfObject" defaultValue="ManagedNumericRepresentation" fixedValue="true"/>
  // d:StructuredMixedResponseDomain/d:ResponseDomainInMixed/d:DateTimeDomainReference/r:TypeOfObject" defaultValue="ManagedDateTimeRepresentation" fixedValue="true"/>
  // d:StructuredMixedResponseDomain/d:ResponseDomainInMixed/d:MissingValueDomainReference/r:TypeOfObject" defaultValue="MissingCodeRepresentation" fixedValue="true"/>
  private val manRep:List<AbstractXmlBuilder>
  override val xmlFragment:String
  get() = ""
  
  init{
    val children:List<Category> = if (entity.responseKind == ResponseKind.MIXED)  {
      responseDomain.managedRepresentation.children!!
    } else{
      mutableListOf<Category>().also {
        it.add(responseDomain.managedRepresentation)
      }
    }
    manRep = children.stream().map {
        FragmentBuilderManageRep(it, this.entity.displayLayout)
      }.collect(Collectors.toList())
  }
  override fun addXmlFragments(fragments:Map<ElementKind, MutableMap<String, String>>) {
    manRep.forEach { it.addXmlFragments(fragments) }
  }
  override fun getXmlEntityRef(depth:Int):String {
    if (entity.responseKind === ResponseKind.MIXED)
    return String.format(xmlMixedRef, getInMixedRef(depth))
    else if (entity.responseKind === ResponseKind.LIST)
    return String.format(xmlCodeDomRef, getTabs(depth),
      entity.managedRepresentation.classificationLevel?.name,
                         getResponseCardinality(depth),
                         String.format(xmlRef, entity.responseKind.ddiRepresentation, getXmlURN(entity), getTabs(depth + 1)))
    else
    return String.format(xmlRef, entity.responseKind.ddiName, getXmlURN(entity), getTabs(depth))
  }
  private fun getResponseCardinality(depth:Int):String {
    return String.format("%3\$s<r:ResponseCardinality minimumResponses = %1\$s maximumResponses = %2\$s />\n",
                         entity.responseCardinality.minimum,
                         entity.responseCardinality.maximum,
                         getTabs(depth))
  }
  override fun <S : AbstractEntityAudit> getXmlHeader(instance:S):String {
    return String.format(xmlHeader, entity.responseKind.ddiName, entity.modified, getXmlURN(entity) + getXmlUserId(entity) + getXmlRationale(entity) + getXmlBasedOn(entity))
  }
//  override fun <S : AbstractEntityAudit> getXmlFooter(instance:S):String {
//    return String.format(xmlFooter, instance.getClass().getSimpleName())
//  }
  private fun getInMixedRef(depth:Int):String {
    return entity.managedRepresentation.children!!.stream().map {
        ref -> String.format(xmlInMixed, ref.xmlBuilder.getXmlEntityRef(depth + 2))
      }.collect(Collectors.joining())
  }
}
