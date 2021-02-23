package no.nsd.qddt.model.embedded

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IElementRef
import org.springframework.data.history.Revision
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable

/**
 * @author Stig Norland
 */
@Embeddable
class ElementRefResponseDomain() : IElementRef<ResponseDomain> , Serializable {

    override lateinit var elementId: UUID
    override var elementRevision: Int? = null
    override var name: String? = null

    @javax.persistence.Transient
    override var version: Version = Version()

    @javax.persistence.Transient
    override var elementKind: ElementKind = ElementKind.RESPONSEDOMAIN

    @javax.persistence.Transient
    @JsonSerialize(contentAs = ResponseDomain::class)
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
