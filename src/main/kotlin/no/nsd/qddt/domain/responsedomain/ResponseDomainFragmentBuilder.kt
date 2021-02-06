package no.nsd.qddt.domain.responsedomain
import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.domain.category.Category
import no.nsd.qddt.domain.classes.elementref.ElementKind
import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.domain.classes.xml.XmlDDIFragmentBuilder
import java.util.stream.Collectors
/**
* @author Stig Norland
*/
class ResponseDomainFragmentBuilder(responseDomain:ResponseDomain):XmlDDIFragmentBuilder<ResponseDomain>(responseDomain) {
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
  val xmlFragment:String
  get() {
    return ""
  }
  init{
    val children:List<Category>
    if (entity.getResponseKind().equals(ResponseKind.MIXED))
    {
      children = responseDomain.getManagedRepresentation().getChildren()
    }
    else
    {
      val categories = java.util.ArrayList<Category>()
      categories.add(responseDomain.getManagedRepresentation())
      children = categories
    }
    manRep = children.stream()
    .map<Any>({ item-> FragmentBuilderManageRep(item, this.entity.getDisplayLayout()) })
    .collect<List<AbstractXmlBuilder>, Any>(Collectors.toList<Any>())
  }
  fun addXmlFragments(fragments:Map<ElementKind, Map<String, String>>) {
    manRep.forEach({ c-> c.addXmlFragments(fragments) })
  }
  fun getXmlEntityRef(depth:Int):String {
    if (entity.getResponseKind() === ResponseKind.MIXED)
    return String.format(xmlMixedRef, getInMixedRef(depth))
    else if (entity.getResponseKind() === ResponseKind.LIST)
    return String.format(xmlCodeDomRef, getTabs(depth),
                         entity.getManagedRepresentation().getClassificationLevel().name(),
                         getResponseCardinality(depth),
                         String.format(xmlRef, entity.getResponseKind().getDdiRepresentation(), getXmlURN(entity), getTabs(depth + 1)))
    else
    return String.format(xmlRef, entity.getResponseKind().getDDIName(), getXmlURN(entity), getTabs(depth))
  }
  private fun getResponseCardinality(depth:Int):String {
    return String.format("%3\$s<r:ResponseCardinality minimumResponses = %1\$s maximumResponses = %2\$s />\n",
                         entity.getResponseCardinality().getMinimum(),
                         entity.getResponseCardinality().getMaximum(),
                         getTabs(depth))
  }
  protected fun <S : AbstractEntityAudit> getXmlHeader(instance:S):String {
    return String.format(xmlHeader, entity.getResponseKind().getDDIName(), entity.getModified(), getXmlURN(entity) + getXmlUserId(entity) + getXmlRationale(entity) + getXmlBasedOn(entity))
  }
  protected fun <S : AbstractEntityAudit> getXmlFooter(instance:S):String {
    return String.format(xmlFooter, instance.getClass().getSimpleName())
  }
  private fun getInMixedRef(depth:Int):String {
    return entity.getManagedRepresentation().getChildren().stream()
    .map({ ref-> String.format(xmlInMixed, ref.getXmlBuilder().getXmlEntityRef(depth + 2)) })
    .collect(Collectors.joining())
  }
}