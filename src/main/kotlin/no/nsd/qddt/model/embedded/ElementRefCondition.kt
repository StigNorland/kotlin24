package no.nsd.qddt.model.embedded

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.ControlConstruct
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IElementRef
import org.hibernate.envers.Audited
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Audited
@Embeddable
class ElementRefCondition : IElementRef<ControlConstruct> , Serializable {
    /**
     * This field will be populated with the correct version of a QI,
     * but should never be persisted.
     */

    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "element_id")),
        AttributeOverride(name = "rev", column = Column(name = "element_revision"))
    )
    @Embedded
    override lateinit var uri: UriId
    @Transient
    @JsonSerialize
    override var version: Version = Version()
    override var name: String? =""


    var condition: String? = null
        set(value){
            field = if (value != null) {
                val min = minOf(value.length, 500)
                value.substring(0, min)
            } else {
                value
            }
        }

    @Transient
    @JsonSerialize
    @Enumerated(EnumType.STRING)
    override var elementKind = ElementKind.CONDITION_CONSTRUCT

    @Transient
    @JsonSerialize
    override var element: ControlConstruct? = null
        set(value) {
            field = value?.also { item ->
                uri = UriId().also {
                    it.id = item.id!!
                    it.rev = item.version.rev
                }
                name = item.label
                version = item.version
            }
            if (value == null) {
                name = null
                condition = ""
            }
        }
}
