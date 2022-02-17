package no.nsd.qddt.model.embedded

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IElementRef
import org.hibernate.envers.Audited
import java.io.Serializable
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 */
//@EntityListeners(value = [QuestionItemRefAuditTrailer::class])
@Audited
@Embeddable
class ElementRefQuestionItem : IElementRef<QuestionItem>, Serializable {
    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "element_id", insertable = false, updatable = false)),
        AttributeOverride(name = "rev", column = Column(name = "element_revision", insertable = false, updatable = false))
    )
    @Embedded
    lateinit var uri: UriId

    @Enumerated(EnumType.STRING)
    override lateinit var elementKind: ElementKind

    @Column(name = "element_name", length = 500)
    override var name: String? = null


    @JsonIgnore
    @Column(name = "element_id")
    override var elementId: UUID?=null

    @Column(name = "element_revision")
    @JsonIgnore
    override var elementRevision: Int? = null


    @AttributeOverrides(
        AttributeOverride(name = "major", column = Column(name = "element_major")),
        AttributeOverride(name = "minor", column = Column(name = "element_minor")),
        AttributeOverride(name = "versionLabel", column = Column(name = "element_version_label")),
        AttributeOverride(name = "rev", column = Column(name = "element_revision", insertable = false, updatable = false))
    )
    @Embedded
    override var version: Version = Version()


    @Transient
    var text: String? = null
        get() {
            return element?.question ?: field
        }


    /**
     * This field will be populated with the correct version of a QI,
     * but should never be persisted.
     */
    @Transient
    @JsonSerialize
    override var element: QuestionItem? = null
        set(value) {
            field = value
            value?.let {
                elementId = it.id
                elementRevision = it.version.rev
                name = it.name
                text = it.question
                version = it.version
            }
            if (value == null) {
                name = ""
                text = ""
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
                this.elementRevision = elementRevision
            
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ElementRefQuestionItem

        if (elementId != other.elementId) return false
        if (elementRevision != other.elementRevision) return false

        return true
    }

    override fun hashCode(): Int {
        var result = elementId?.hashCode() ?: 0
        result = 31 * result + (elementRevision ?: 0)
        return result
    }


}
