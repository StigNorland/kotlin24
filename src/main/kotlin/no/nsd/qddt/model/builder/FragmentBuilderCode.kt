package no.nsd.qddt.model.builder
import no.nsd.qddt.model.Category

/**
* @author Stig Norland
*/
class FragmentBuilderCode(entity: Category): CategoryFragmentBuilder(entity) {
  private val xmlCode = (
    "%4\$s<l:Code scopeOfUniqueness=\"Maintainable\" isUniversallyUnique=\"false\" isIdentifiable=\"true\" isDiscrete=\"true\" levelNumber=\"1\" isTotal=\"false\">\n" +
    "%4\$s\t%1\$s" +
    "%2\$s" +
    "%4\$s\t<r:Value xml:space=\"default\">%3\$s</r:Value>\n" +
    "%4\$s</l:Code>\n")

  override val xmlFragment:String
  get() = super.xmlFragment

  private val codeURN:String
  get() = String.format(xmlURN, entity.agency.name, entity.id, entity.code.value)

  override fun getXmlEntityRef(depth:Int):String {
    return String.format(xmlCode, codeURN, super.getXmlEntityRef(depth + 1), entity.code.value.trim(), getTabs(depth))
  }
}
