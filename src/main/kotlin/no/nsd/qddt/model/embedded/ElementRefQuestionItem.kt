package no.nsd.qddt.model.embedded

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IElementRef
import org.hibernate.envers.Audited
import java.io.Serializable
import javax.persistence.*

/**
 * @author Stig Norland
 */

//@EntityListeners(value = [QuestionItemRefAuditTrailer::class])
@Audited
@Embeddable
class ElementRefQuestionItem : IElementRef<QuestionItem>, Serializable {
    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "element_id")),
        AttributeOverride(name = "rev", column = Column(name = "element_revision"))
    )
    @Embedded
    override lateinit var uri: UriId

    @Enumerated(EnumType.STRING)
    override lateinit var elementKind: ElementKind

    @Column(name = "element_name", length = 500)
    override var name: String? = null


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
            value?.let { item ->
                uri = UriId().also {
                    it.id = item.id!!
                    it.rev = item.version.rev
                }
                name = item.name
                text = item.question
                version = item.version
            }
        }

    public override fun clone(): ElementRefQuestionItem {
        return ElementRefQuestionItem().apply {
            this.version = version
            this.name =name
            if (element != null)
                this.element = element
            else
                this.uri = uri
                this.elementKind = elementKind
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ElementRefQuestionItem) return false

        if (uri != other.uri) return false
        if (elementKind != other.elementKind) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uri.hashCode()
        result = 31 * result + elementKind.hashCode()
        return result
    }


}
