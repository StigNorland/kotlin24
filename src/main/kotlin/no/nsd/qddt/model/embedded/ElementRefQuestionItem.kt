package no.nsd.qddt.model.embedded

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.interfaces.IElementRef
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.enums.ElementKind
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Embeddable
class ElementRefQuestionItem : IElementRef<QuestionItem> {
    /**
     * This field will be populated with the correct version of a QI,
     * but should never be persisted.
     */

    override lateinit var elementId: UUID

    @Embedded
    @Transient
    @JsonSerialize
    override lateinit var version: Version

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
                version = it.version
            }
            if (value == null) {
                name = ""
                text = ""
                elementRevision = null
            }
        }
}
