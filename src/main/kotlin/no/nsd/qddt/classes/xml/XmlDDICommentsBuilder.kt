package no.nsd.qddt.classes.xml

import no.nsd.qddt.domain.Comment
import no.nsd.qddt.classes.elementref.ElementKind

/**
 * @author Stig Norland
 */
class XmlDDICommentsBuilder(protected val comment: Comment) : AbstractXmlBuilder() {
    
    override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {
//        add nothing  ATM
//        fragments.get(ElementKind.getEnum( entity.getClassKind())).putIfAbsent( getUrnId(), getXmlFragment() );
    }

    override fun getXmlEntityRef(depth: Int): String {
        return "null"
    }

    override val xmlFragment: String
        get() = "null"

}
