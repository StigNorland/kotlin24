package no.nsd.qddt.domain.classes.xml

import no.nsd.qddt.domain.classes.elementref.ElementKind
import no.nsd.qddt.domain.comment.Comment

/**
 * @author Stig Norland
 */
class XmlDDICommentsBuilder(comment: Comment) : AbstractXmlBuilder() {
    protected val comment: Comment
    override fun addXmlFragments(fragments: Map<ElementKind?, MutableMap<String?, String>>) {
//        add nothing  ATM
//        fragments.get(ElementKind.getEnum( entity.getClassKind())).putIfAbsent( getUrnId(), getXmlFragment() );
    }

    override fun getXmlEntityRef(depth: Int): String? {
        return null
    }

    override val xmlFragment: String?
        get() = null

    init {
        this.comment = comment
    }
}
