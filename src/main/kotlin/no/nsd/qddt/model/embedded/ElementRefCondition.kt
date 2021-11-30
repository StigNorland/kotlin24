package no.nsd.qddt.model.embedded

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.ControlConstruct
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IElementRef
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Transient

@Embeddable
class ElementRefCondition : IElementRef<ControlConstruct> , Serializable {
    /**
     * This field will be populated with the correct version of a QI,
     * but should never be persisted.
     */

    override var elementId: UUID?=null
    override var elementRevision: Int? = null
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
            field = value?.also {
                elementId = it.id
                name = it.label
                version = it.version
                if (version.rev == 0)
                    version.rev = elementRevision?:0
            }
            if (value == null) {
                name = null
                condition = ""
                elementRevision = null
            }
        }
}
