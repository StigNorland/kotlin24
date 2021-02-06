package no.nsd.qddt.domain.responsedomain
import no.nsd.qddt.domain.category.Category
import no.nsd.qddt.domain.category.CategoryFragmentBuilder
/**
* @author Stig Norland
*/
class FragmentBuilderCode(entity:Category):CategoryFragmentBuilder(entity) {
  private val xmlCode = (
    "%4\$s<l:Code scopeOfUniqueness=\"Maintainable\" isUniversallyUnique=\"false\" isIdentifiable=\"true\" isDiscrete=\"true\" levelNumber=\"1\" isTotal=\"false\">\n" +
    "%4\$s\t%1\$s" +
    "%2\$s" +
    "%4\$s\t<r:Value xml:space=\"default\">%3\$s</r:Value>\n" +
    "%4\$s</l:Code>\n")
  val xmlFragment:String
  get() {
    return super.getXmlFragment()
  }
  private val codeURN:String
  get() {
    return String.format(xmlURN, entity.getAgency().getName(), entity.getId(), entity.getCode().getValue())
  }
  fun getXmlEntityRef(depth:Int):String {
    return String.format(xmlCode, codeURN, super.getXmlEntityRef(depth + 1), entity.getCode().getValue().trim(), getTabs(depth))
  }
}