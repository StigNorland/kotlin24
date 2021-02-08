package no.nsd.qddt.domain.classes.elementref

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.domain.classes.interfaces.IElementRef
import no.nsd.qddt.domain.classes.interfaces.Version
import no.nsd.qddt.domain.responsedomain.ResponseDomain
import org.hibernate.annotations.Type
import org.springframework.data.history.Revision
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

/**
 * @author Stig Norland
 */
@Embeddable
class ElementRefResponseDomain : IElementRef<ResponseDomain?> {

    @Column(name = "responsedomain_id")
    var elementId: UUID? = null

    @Column(name = "responsedomain_revision")
    var elementRevision: Int? = null

    @Column(name = "responsedomain_name")
    var name: String? = null

    constructor(responseDomain: ResponseDomain?) {
        setElement(responseDomain)
    }

    constructor(revision: Revision<Comparable<Int>, ResponseDomain?>) {
        elementRevision = revision.getRevisionNumber().get()
        setElement(revision.getEntity())
    }

    @Transient
    override var elementKind: ElementKind = ElementKind.RESPONSEDOMAIN



    override fun setElement(element: ResponseDomain?) {
        this.element = element
        if (element != null) {
            setElementId(getElement().getId())
            name = this.element.name
            version.setRevision(elementRevision)
        } else {
            name = null
            elementRevision = null
            setElementId(null)
        }
    }

    override var version: Version
        get() = TODO("Not yet implemented")
        set(value) {}
    override var element: ResponseDomain?
        get() = TODO("Not yet implemented")
        set(value) {}


}
