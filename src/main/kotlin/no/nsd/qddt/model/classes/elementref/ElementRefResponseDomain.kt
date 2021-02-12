package no.nsd.qddt.model.classes.elementref

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.interfaces.IElementRef
import no.nsd.qddt.model.classes.Version
import no.nsd.qddt.model.ResponseDomain
import org.springframework.data.history.Revision
import java.util.*
import javax.persistence.Embeddable

/**
 * @author Stig Norland
 */
@Embeddable
class ElementRefResponseDomain() : IElementRef<ResponseDomain> {

    override lateinit var elementId: UUID
    override var elementRevision: Int? = null
    override var name: String? = null

    @javax.persistence.Transient
    override var version: Version = Version(1,0,0,"")

    @javax.persistence.Transient
    override var elementKind: ElementKind = ElementKind.RESPONSEDOMAIN

    @javax.persistence.Transient
    @JsonSerialize
    override var element: ResponseDomain? = null
        set(value) {
            field = value
            value?.let {
                elementId = it.id
                name = it.name
                version = it.version
            }
            if (value == null) {
                name = ""
                elementRevision = null
            }
        }

    constructor(revision: Revision<Int, ResponseDomain>) : this() {
        elementRevision = revision.revisionNumber.get()
        element = revision.entity
    }
    

}
