package no.nsd.qddt.model.embedded

import no.nsd.qddt.model.classes.AbstractElementRef
import no.nsd.qddt.model.interfaces.IDomainObject
import org.hibernate.envers.Audited
import javax.persistence.Embeddable


/**
 * @author Stig Norland
 */
@Audited
@Embeddable
class PublicationElement(element: IDomainObject) : AbstractElementRef<IDomainObject>(element) {

    public override fun clone(): PublicationElement {
        return PublicationElement(element!!)
    }

}
