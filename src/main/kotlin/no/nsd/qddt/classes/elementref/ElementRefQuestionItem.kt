package no.nsd.qddt.classes.elementref

import no.nsd.qddt.classes.interfaces.Version
import no.nsd.qddt.domain.questionitem.QuestionItem
import org.hibernate.annotations.Type
import javax.persistence.Column
import javax.persistence.EnumType
import javax.persistence.Transient

/**
 * @author Stig Norland
 */
@Embeddable
class ElementRefQuestionItem : IElementRef<QuestionItem?> {
    /**
     * This field will be populated with the correct version of a QI,
     * but should never be persisted.
     */
    @Transient
    @JsonSerialize
    private var element: QuestionItem? = null

    @Type(type = "pg-uuid")
    @Column(name = "questionitem_id")
    private var elementId: UUID? = null

    @Column(name = "questionitem_revision")
    var elementRevision: Int? = null

    @Column(name = "question_name", length = 25)
    private var name: String? = null

    @Column(name = "question_text", length = 500)
    private var text: String? = null

    @Transient
    @JsonSerialize
    @Enumerated(EnumType.STRING)
    val elementKind = ElementKind.QUESTION_ITEM

    constructor() {}
    constructor(questionItem: QuestionItem?) {
        setElement(questionItem)
    }

    override fun getElementId(): UUID? {
        return elementId
    }

    private fun setElementId(id: UUID?) {
        elementId = id
    }

    override fun name: String? {
        return name
    }

    fun setName(name: String?) {
        if (name != null) {
            val min = Integer.min(name.length, 24)
            this.name = name.substring(0, min)
        } else {
            this.name = null
        }
    }

    fun setText(text: String?) {
        if (text != null) {
            val min = Integer.min(text.length, 500)
            this.text = text.substring(0, min)
        } else {
            this.text = null
        }
    }

    fun getText(): String {
        return if (element != null) element.getQuestion() else text!!
    }

    val version: Version?
        get() = if (element == null) null else element.version

    override fun getElement(): QuestionItem? {
        return element
    }

    override fun setElement(element: QuestionItem?) {
        this.element = element
        if (element != null) {
            setElementId(element.getId())
            setName(element.name)
            setText(element.getQuestion())
            version.setRevision(elementRevision)
        } else {
            setName(null)
            setText(null)
            elementRevision = null
            setElementId(null)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ElementRefQuestionItem
        if (if (element != null) !element.equals(that.element) else that.element != null) return false
        if (if (elementId != null) elementId != that.elementId else that.elementId != null) return false
        if (if (elementRevision != null) elementRevision != that.elementRevision else that.elementRevision != null) return false
        if (if (name != null) name != that.name else that.name != null) return false
        return if (text != null) text == that.text else that.text == null
    }

    override fun hashCode(): Int {
        var result = if (element != null) element.hashCode() else 0
        result = 31 * result + if (elementId != null) elementId.hashCode() else 0
        result = 31 * result + if (elementRevision != null) elementRevision.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + if (text != null) text.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "{" +
                "\"elementId\":" + (if (elementId == null) "null" else elementId) + ", " +
                "\"elementRevision\":" + (if (elementRevision == null) "null" else "\"" + elementRevision + "\"") + ", " +
                "\"name\":" + (if (name == null) "null" else "\"" + name + "\"") + ", " +
                "\"elementKind\":" + (elementKind ?: "null") +
                "}"
    }
}
