package no.nsd.qddt.model.embedded

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IElementRef
import org.springframework.data.history.Revision
import java.io.Serializable
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Embeddable
class ElementRefResponseDomain() : IElementRef<ResponseDomain> , Serializable {

    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "element_id")),
        AttributeOverride(name = "rev", column = Column(name = "element_revision"))
    )
    @Embedded
    override lateinit var uri: UriId
    override var name: String? = null

    @Transient
    override var version: Version = Version()

    @Transient
    override var elementKind: ElementKind = ElementKind.RESPONSEDOMAIN

    @Transient
    @JsonSerialize(contentAs = ResponseDomain::class)
    override var element: ResponseDomain? = null
        set(value) {
            field = value?.also { item ->
                uri = UriId().also {
                    it.id = item.id!!
                    it.rev = item.version.rev
                }
                name = item.name
                version = item.version
            }
//            if (value == null) {
//                name = ""
//            }
        }

    constructor(revision: Revision<Int, ResponseDomain>) : this() {
        element = revision.entity
    }
    

}
