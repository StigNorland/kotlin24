package no.nsd.qddt.model.embedded

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IElementRef
import java.io.Serializable
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 */
//@EntityListeners(value = [QuestionItemRefAuditTrailer::class])
@Embeddable
class ElementRefQuestionItem : IElementRef<QuestionItem>, Serializable {
    /**
     * This field will be populated with the correct version of a QI,
     * but should never be persisted.
     */

    override var elementId: UUID?=null

    @Embedded
    @Transient
    @JsonSerialize
    override var version: Version = Version()

    override var elementRevision: Int? = null

    override var name: String? = null
        set(value) {
            field = value
            value?.let {
                val min = Integer.min(it.length, 24)
                field = it.substring(0, min)
            }
        }

    var text: String? = null
        set(value) {
            field = value
            value?.let {
                val min = Integer.min(it.length, 500)
                field = it.substring(0, min)
            }
        }
        get() {
            return element?.question ?: field
        }

    @Transient
    @JsonSerialize
    @Enumerated(EnumType.STRING)
    override var elementKind = ElementKind.QUESTION_ITEM

    @Transient
    @JsonSerialize
    override var element: QuestionItem? = null
        set(value) {
            field = value
            value?.let {
                elementId = it.id
                name = it.name
                text = it.question
                version = it.version!!
            }
            if (value == null) {
                name = ""
                text = ""
                elementRevision = null
            }
        }

    public override fun clone(): ElementRefQuestionItem {
        return ElementRefQuestionItem().apply {
            this.version = version
            this.name =name
            if (element != null)
                this.element = element
            else
                this.elementId = elementId
            
        }
    }
    
}
