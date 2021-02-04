package no.nsd.qddt.domain.classes.elementref

import no.nsd.qddt.domain.classes.interfaces.Version
import no.nsd.qddt.domain.controlconstruct.pojo.ControlConstruct
import org.hibernate.annotations.Type
import javax.persistence.Column
import javax.persistence.EnumType
import javax.persistence.Transient

/**
 * @author Stig Norland
 */
@Embeddable
class ElementRefCondition<T : ControlConstruct?> : IElementRef<T> {
    /**
     * This field will be populated with the correct version of a QI,
     * but should never be persisted.
     */
    @Transient
    @JsonSerialize
    private var element: T? = null

    @Type(type = "pg-uuid")
    @Column(name = "questionitem_id")
    private var elementId: UUID? = null

    @Column(name = "questionitem_revision")
    var elementRevision: Int? = null

    @Column(name = "question_name", length = 25)
    var name: String? = null

    @Column(name = "question_text", length = 500)
    private var condition: String? = null

    @Transient
    @JsonSerialize
    @Enumerated(EnumType.STRING)
    val elementKind = ElementKind.CONDITION_CONSTRUCT
    override fun getElementId(): UUID? {
        return elementId
    }

    private fun setElementId(id: UUID?) {
        elementId = id
    }

    fun getCondition(): String? {
        return condition
        //        return (this.element != null) ? this.element.get() : this.condition;
    }

    fun setCondition(condition: String?) {
        if (condition != null) {
            val min = Integer.min(condition.length, 500)
            this.condition = condition.substring(0, min)
        } else {
            this.condition = null
        }
    }

    val version: Version?
        get() = if (element == null) null else element.getVersion()

    override fun getElement(): T? {
        return element
    }

    override fun setElement(element: T?) {
        this.element = element
        if (element != null) {
            setElementId(element.getId())
            name = element.getName()
            //            setCondition( element.getCondition() );
            version.setRevision(elementRevision)
        } else {
            name = null
            setCondition(null)
            elementRevision = null
            setElementId(null)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ElementRefCondition<*>
        if (if (element != null) !element.equals(that.element) else that.element != null) return false
        if (if (elementId != null) elementId != that.elementId else that.elementId != null) return false
        if (if (elementRevision != null) elementRevision != that.elementRevision else that.elementRevision != null) return false
        if (if (name != null) name != that.name else that.name != null) return false
        return if (condition != null) condition == that.condition else that.condition == null
    }

    override fun hashCode(): Int {
        var result = if (element != null) element.hashCode() else 0
        result = 31 * result + if (elementId != null) elementId.hashCode() else 0
        result = 31 * result + if (elementRevision != null) elementRevision.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + if (condition != null) condition.hashCode() else 0
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
