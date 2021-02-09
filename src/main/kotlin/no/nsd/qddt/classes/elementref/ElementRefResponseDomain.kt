package no.nsd.qddt.classes.elementref

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.classes.interfaces.IElementRef
import no.nsd.qddt.classes.interfaces.Version
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
class ElementRefResponseDomain : IElementRef<ResponseDomain> {

    @Column(name = "responsedomain_name")
    override var name: String? = null

    override var version: Version = Version(1,0,0,"")

    @Column(name = "responsedomain_id")
    override var elementId: UUID? = null

    @Column(name = "responsedomain_revision")
    override var elementRevision: Int? = null


    @Transient
    override var elementKind: ElementKind = ElementKind.RESPONSEDOMAIN

    override var element: ResponseDomain? = null
        set(value) {
            field = value
            if (value != null) {
                elementId  = value.id
                name = value.name
                version = value.version
                version.revision = elementRevision?:0
            } else {
                name = null
                elementRevision = null
                elementId = null
            }
    
        }

    constructor(){}

    constructor(responseDomain: ResponseDomain) {
        element = responseDomain
    }

    constructor(revision: Revision<Int, ResponseDomain>) {
        elementRevision = revision.revisionNumber.get()
        element = revision.entity
    }
    

}
