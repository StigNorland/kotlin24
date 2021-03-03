package no.nsd.qddt.model.builder
import no.nsd.qddt.model.Category
import java.util.Collections
/**
* @author Stig Norland
*/
class FragmentBuilderAnchor(entity: Category): CategoryFragmentBuilder(entity) {
  private val xmlAnchor = (
    "%3\$s<r:Anchor value=\"%1\$s\">\n" +
    "%2\$s" +
    "%3\$s</r:Anchor>\n")

  override fun getXmlEntityRef(depth:Int):String {
    return String.format(
      xmlAnchor,
      entity.code?.value?:"<unset>",
      super.getXmlEntityRef(depth + 1),
      Collections.nCopies(depth, "\t").joinToString("")
    )
  }
}
