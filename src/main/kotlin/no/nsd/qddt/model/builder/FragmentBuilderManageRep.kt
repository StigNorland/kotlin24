package no.nsd.qddt.model.builder

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.enums.CategoryType
import no.nsd.qddt.model.enums.ElementKind
import org.slf4j.LoggerFactory
import java.util.stream.Collectors

/**
* @author Stig Norland
*/
class FragmentBuilderManageRep(entity: Category, degreeSlopeFromHorizontal:String):XmlDDIFragmentBuilder<Category>(entity) {
  protected val LOG = LoggerFactory.getLogger(this.javaClass)

  private val xmlScaleMan = (
    "\t\t\t<r:ScaleDimension dimensionNumber=\"1\" degreeSlopeFromHorizontal=\"%1\$s\">\n" +
    "\t\t\t\t<r:Range>\n" +
    "\t\t\t\t\t<r:RangeUnit>\"%2\$s\"</r:RangeUnit>\n" +
    "\t\t\t\t\t<r:MinimumValue included=\"true\">\"%3\$s\"</r:MinimumValue>\n" +
    "\t\t\t\t\t<r:MaximumValue included=\"true\">\"%4\$s\"</r:MaximumValue>\n" +
    "\t\t\t\t</r:Range>\n" +
    "%5\$s" +
    "\t\t\t</r:ScaleDimension>\n")

    private val xmlMissingMan = (
    "%1\$s<r:MissingCodeRepresentation blankIsMissingValue =\"false\">\n" +
    "%1\$s\t<r:CodeListReference>\n" +
    "%1\$s\t\t%2\$s" +
    "%1\$s\t\t<r:TypeOfObject>CodeList</r:TypeOfObject>\n" +
    "%1\$s\t</r:CodeListReference>\n" +
    "%1\$s</r:MissingCodeRepresentation>\n")

    private val xmlManaged = (
    "%2\$s" +
    "\t\t\t<r:Managed%1\$sRepresentationName>\n" +
    "\t\t\t\t<r:String xml:lang=\"%3\$s\">%4\$s</r:String>\n" +
    "\t\t\t</r:Managed%1\$sRepresentationName>\n" +
    "\t\t\t<r:Label>\n" +
    "\t\t\t\t<r:Content xml:lang=\"%3\$s\">%6\$s</r:Content>\n" +
    "\t\t\t</r:Label>\n" +
    "\t\t\t<r:Description>\n" +
    "\t\t\t\t<r:Content xml:lang=\"%3\$s\">%5\$s</r:Content>\n" +
    "\t\t\t</r:Description>\n" +
    "%7\$s" +
    "\t\t</r:Managed%1\$sRepresentation>\n")
  // d:CodeDomain/r:CodeListReference

  private val xmlCodeList = (
    "%2\$s" +
    "\t\t\t<l:CodeListName>\n" +
    "\t\t\t\t<r:String xml:lang=\"%3\$s\">%4\$s</r:String>\n" +
    "\t\t\t</l:CodeListName>\n" +
    "\t\t\t<r:Label>\n" +
    "\t\t\t\t<r:Content xml:lang=\"%3\$s\">%6\$s</r:Content>\n" +
    "\t\t\t</r:Label>\n" +
    "\t\t\t<r:Description>\n" +
    "\t\t\t\t<r:Content xml:lang=\"%3\$s\">%5\$s</r:Content>\n" +
    "\t\t\t</r:Description>\n" +
    "%7\$s" +
    "\t\t</l:CodeList>\n")

  private val xmlMissingCodeListFragment = (
    "%1\$s<l:CodeList isUniversallyUnique=\"false\" scopeOfUniqueness=\"Maintainable\">\n" +
    "%1\$s\t%2\$s" +
    "%3\$s" +
    "%1\$s</l:CodeList>\n")

  protected val xmlHeaderMR = ("\t\t<%1\$s isUniversallyUnique=\"true\" versionDate=\"%2\$s\" isMaintainable=\"true\" %3\$s>\n" + "%4\$s")

  private val xmlNumeric = " format=\"%1\$s\" scale=1 interval=%2\$s classificationLevel=\"Continuous\" "

  private val xmlText = " maxLength=%1\$s minLength=%2\$s classificationLevel=\"Nominal\" "

  private val children:List<AbstractXmlBuilder>

  private val degreeSlopeFromHorizontal:String = degreeSlopeFromHorizontal

  override val xmlFragment:String
  get() {
    return if (entity.categoryKind === CategoryType.LIST)
      getXmlCodeList()
    else
      String.format(
        xmlManaged,
        entity.categoryKind.name,
        getXmlHeader<AbstractEntityAudit>(entity),
        entity.xmlLang,
        entity.name,
        entity.description,
        entity.label,
        this.xmlRefs)
  }
  private val xmlRefs:String
  get() {
    when(entity.categoryKind) {
        CategoryType.SCALE ->
          return String.format(
            xmlScaleMan,
            degreeSlopeFromHorizontal,
            entity.inputLimit.stepUnit,
            entity.inputLimit.minimum,
            entity.inputLimit.maximum,
            children.stream()
              .map{ q-> q.getXmlEntityRef(4) }
              .collect(Collectors.joining()))
        CategoryType.MISSING_GROUP ->
          return String.format(xmlMissingMan, getTabs(3), missingCodeURN, getMissingXmlEntityRef(4))
        else ->
          return children.stream()
          .map { q-> q.getXmlEntityRef(3) }
          .collect(Collectors.joining())
    }
  }

  private val missingCodeURN:String
  get() {
    return String.format(xmlURN, entity.agency.name, entity.name, entity.version.toDDIXml())
  }

  init{
    children = entity.children.stream()
        .map { this.getBuilder(it) }
        ?.collect(Collectors.toList()) ?: mutableListOf()
  }

  override fun <S : AbstractEntityAudit> getXmlHeader(instance:S):String {
    val ref = instance as Category
    val attr = when {
        entity.categoryKind === CategoryType.NUMERIC -> String.format(xmlNumeric, ref.format, ref.inputLimit.stepUnit)
        entity.categoryKind === CategoryType.TEXT -> String.format(xmlText, ref.inputLimit.maximum, ref.inputLimit.minimum)
        else -> ""
    }
    if (entity.categoryKind == CategoryType.LIST)
      return String.format(
        xmlHeaderMR, "l:" + entity.categoryKind.name,
        getInstanceDate(instance),
        "",
        "\t\t\t" 
         + getXmlURN(instance)
         + getXmlUserId(instance) 
         + getXmlRationale(instance)
         + getXmlBasedOn(instance))
    else
      return String.format(
        xmlHeaderMR, "r:Managed" 
          + entity.categoryKind.name
          + "Representation",
          getInstanceDate(instance),
          attr,
          "\t\t\t" 
            + getXmlURN(instance) 
            + getXmlUserId(instance) 
            + getXmlRationale(instance) 
            + getXmlBasedOn(instance))
  }

  override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {
    super.addXmlFragments(fragments)
    if (entity.categoryKind === CategoryType.MISSING_GROUP)
    (fragments.get(ElementKind.CATEGORY))!!.putIfAbsent(missingCodeURN, getXmlMissingCodeListFragment())
    children.forEach { c -> c.addXmlFragments(fragments) }
  }

  private fun getXmlCodeList():String {
    return String.format(
      xmlCodeList,
      entity.categoryKind.name,
      getXmlHeader<AbstractEntityAudit>(entity),
      entity.xmlLang,
      entity.name,
      entity.description,
      entity.label,
      children.stream()
      .map { q-> q.getXmlEntityRef(3) }
      .collect(Collectors.joining()))
  }

  private fun getBuilder(category: Category):AbstractXmlBuilder {
    return when (entity.categoryKind) {
      CategoryType.SCALE -> FragmentBuilderAnchor(category)
      CategoryType.MISSING_GROUP,
      CategoryType.LIST -> FragmentBuilderCode(category)
      else -> {
        LOG.info("getbuilder get default")
        category.xmlBuilder()
      }
    }
  }

  private fun getXmlMissingCodeListFragment():String {
    return String.format(
      xmlMissingCodeListFragment,
      getTabs(2),
      missingCodeURN,
      children.stream()
      .map{ q-> q.getXmlEntityRef(3) }
      .collect(Collectors.joining()))
  }

  private fun getMissingXmlEntityRef(depth:Int):String {
    return String.format(xmlMissingMan, getTabs(depth), missingCodeURN)
  }
}
