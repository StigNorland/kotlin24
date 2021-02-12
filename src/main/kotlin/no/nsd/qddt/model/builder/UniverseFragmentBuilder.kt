package no.nsd.qddt.model.builder;

import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder;
import no.nsd.qddt.model.Universe

/**
 * @author Stig Norland
 */
class UniverseFragmentBuilder(entity: Universe):XmlDDIFragmentBuilder<Universe>(entity) {
  
  private val xmlUniverse = (
    "%1\$s" +
    "\t\t\t<c:UniverseName>\n" +
    "\t\t\t\t<r:String xml:lang=\"%4\$s\">%2\$s</r:String>\n" +
    "\t\t\t</c:UniverseName>\n" +
    "\t\t\t<r:Description>\n" +
    "\t\t\t\t<r:Content xml:lang=\"%4\$s\" isPlainText=\"false\"><![CDATA[%3\$s]]></r:Content>\n" +
    "\t\t\t</r:Description>\n" +
    "\t\t</c:Universe>\n")
  
  override val xmlFragment:String
  get() {
    return String.format(
      xmlUniverse,
      getXmlHeader(entity),
      entity.name,
      entity.description,
      entity.xmlLang)
  }
}
