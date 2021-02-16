package no.nsd.qddt.model.classes.elementref

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.ControlConstruct
import no.nsd.qddt.model.interfaces.IElementRef
import no.nsd.qddt.model.classes.Version
import java.util.*
import javax.persistence.*

@Embeddable
class ElementRefCondition : IElementRef<ControlConstruct> {
    /**
     * This field will be populated with the correct version of a QI,
     * but should never be persisted.
     */

    override lateinit var elementId: UUID
    override var elementRevision: Int? = -1
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
                name = it.name
                version.revision = elementRevision?:0
            }
            if (value == null) {
                name = null
                condition = ""
                elementRevision = null
            }
        }
}
