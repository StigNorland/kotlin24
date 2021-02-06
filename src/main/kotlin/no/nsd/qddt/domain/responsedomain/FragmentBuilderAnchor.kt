package no.nsd.qddt.domain.responsedomain
import no.nsd.qddt.domain.category.Category
import no.nsd.qddt.domain.category.CategoryFragmentBuilder
import java.util.Collections
/**
* @author Stig Norland
*/
class FragmentBuilderAnchor(entity:Category):CategoryFragmentBuilder(entity) {
  private val xmlAnchor = (
    "%3\$s<r:Anchor value=\"%1\$s\">\n" +
    "%2\$s" +
    "%3\$s</r:Anchor>\n")
  fun getXmlEntityRef(depth:Int):String {
    return String.format(xmlAnchor, entity.getCode().getValue(), super.getXmlEntityRef(depth + 1), Collections.nCopies<String>(depth, "\t").joinToString(""))
  }
}