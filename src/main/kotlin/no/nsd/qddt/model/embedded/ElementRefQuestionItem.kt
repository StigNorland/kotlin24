package no.nsd.qddt.model.embedded

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.QuestionItem
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
    /**
     * This field will be populated with the correct version of a QI,
     * but should never be persisted.
     */

    override var elementId: UUID?=null

    @Enumerated(EnumType.STRING)
    override lateinit var elementKind: ElementKind

    @Column(name = "element_revision", insertable =false, updatable=false )
    override var elementRevision: Int? = null

    @Column(name = "element_name", length = 500)
    override var name: String? = null


    @AttributeOverrides(
        AttributeOverride(name = "major",       column = Column(name = "element_major")),
        AttributeOverride(name = "minor",       column = Column(name = "element_minor")),
        AttributeOverride(name = "rev",         column = Column(name = "element_revision")),
        AttributeOverride(name = "versionLabel",column = Column(name = "element_version_label"))
    )
    @Transient
    override var version: Version = Version()

    @Transient
    var text: String? = null
        get() {
            return element?.question ?: field
        }


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
