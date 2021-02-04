package no.nsd.qddt.domain.classes.elementref

import no.nsd.qddt.domain.classes.interfaces.Version
import no.nsd.qddt.domain.responsedomain.ResponseDomain
import org.hibernate.annotations.Type
import javax.persistence.Column

/**
 * @author Stig Norland
 */
@Embeddable
class ElementRefResponseDomain : IElementRef<ResponseDomain?> {
    @javax.persistence.Transient
    @JsonSerialize
    private var element: ResponseDomain? = null

    //    @JsonIgnore
    @Type(type = "pg-uuid")
    @Column(name = "responsedomain_id")
    private var elementId: UUID? = null

    @Column(name = "responsedomain_revision")
    var elementRevision: Int? = null

    @Column(name = "responsedomain_name")
    var name: String? = null

    constructor() {}
    constructor(responseDomain: ResponseDomain?) {
        setElement(responseDomain)
    }

    constructor(revision: Revision<Int?, ResponseDomain?>) {
        elementRevision = revision.getRevisionNumber().get()
        setElement(revision.getEntity())
    }

    @get:Transient
    val elementKind: ElementKind?
        get() = ElementKind.RESPONSEDOMAIN

    override fun getElementId(): UUID? {
        return elementId
    }

    val version: Version?
        get() = if (element == null) null else element.getVersion()

    override fun getElement(): ResponseDomain? {
        return element
    }

    override fun setElement(element: ResponseDomain?) {
        this.element = element
        if (element != null) {
            setElementId(getElement().getId())
            name = this.element.getName()
            version.setRevision(elementRevision)
        } else {
            name = null
            elementRevision = null
            setElementId(null)
        }
    }

    fun setElementId(elementId: UUID?) {
        this.elementId = elementId
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ElementRefResponseDomain
        if (if (element != null) !element.equals(that.element) else that.element != null) return false
        if (if (elementId != null) elementId != that.elementId else that.elementId != null) return false
        if (if (elementRevision != null) elementRevision != that.elementRevision else that.elementRevision != null) return false
        return if (name != null) name == that.name else that.name == null
    }

    override fun hashCode(): Int {
        var result = if (element != null) element.hashCode() else 0
        result = 31 * result + if (elementId != null) elementId.hashCode() else 0
        result = 31 * result + if (elementRevision != null) elementRevision.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "{" +
                "\"elementId\":" + (if (elementId == null) "null" else elementId) + ", " +
                "\"elementRevision\":" + (if (elementRevision == null) "null" else "\"" + elementRevision + "\"") + ", " +
                "\"name\":" + (if (name == null) "null" else "\"" + name + "\"") + ", " +
                "\"elementKind\":" + (if (elementKind == null) "null" else elementKind) +
                "}"
    }
}
