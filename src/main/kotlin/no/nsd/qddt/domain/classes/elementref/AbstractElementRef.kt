package no.nsd.qddt.domain.classes.elementref

/**
 * @author Stig Norland
 */
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.domain.classes.interfaces.IElementRef
import no.nsd.qddt.domain.classes.interfaces.IWebMenuPreview
import no.nsd.qddt.domain.classes.interfaces.Version
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@MappedSuperclass
abstract class AbstractElementRef<T : IWebMenuPreview?> : IElementRef<T> {
    @Enumerated(EnumType.STRING)
    override var elementKind: ElementKind? = null

    @Type(type = "pg-uuid")
    var elementId: UUID? = null

    @Column(name = "element_revision")
    var elementRevision: Int? = null

    @Column(name = "element_name", length = 500)
    var name: String? = null

    @Column(name = "element_major")
    private var major: Int? = null

    @Column(name = "element_minor")
    private var minor: Int? = null

    @Column(name = "element_version_label")
    private var versionLabel: String? = null

    @Transient
    @JsonSerialize
    @JsonDeserialize
    protected var element: T? = null


    @JsonCreator
    constructor(
        @JsonProperty("elementKind") kind: ElementKind?,
        @JsonProperty("id") id: UUID?,
        @JsonProperty("revisionNumber") rev: Int?
    ) {
        elementKind = kind
        setElementId(id)
        elementRevision = rev
    }


    val version: Version?
        get() = Version(major, minor, elementRevision, versionLabel)

    fun setVersion(version: Version) {
        major = version.major
        minor = version.minor
        versionLabel = version.versionLabel
    }

    fun getElement(): T? {
        return element
    }

    override fun setElement(element: T?) {
        this.element = element
        setValues()
    }

    protected open fun setValues(): AbstractElementRef<T> {
        if (getElement() == null) return this
        if (StringTool.IsNullOrEmpty(name)) {
            if (element is QuestionItem) name =
                getElement().name + " ➫ " + (element as QuestionItem?).getQuestion() else if (element is StatementItem) name =
                getElement().name + " ➫ " + (element as StatementItem?).getStatement() else if (element is ConditionConstruct) {
                name = getElement().name + " ➫ " + (element as ConditionConstruct?).getCondition()
                println(
                    ElementKind.Companion.getEnum(element.javaClass.getSimpleName())
                        .toString() + " - ConditionConstruct- name set"
                )
            } else if (element is QuestionConstruct) {
                println(
                    ElementKind.Companion.getEnum(element.javaClass.getSimpleName())
                        .toString() + " - QuestionConstruct name not set"
                )
            } else println(ElementKind.Companion.getEnum(element.javaClass.getSimpleName()).toString() + " - set name")
            name = getElement().name
        }
        setVersion(getElement().getVersion())
        if (elementKind == null) elementKind = ElementKind.Companion.getEnum(element.javaClass.getSimpleName())
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is AbstractElementRef<*>) return false
        val that: AbstractElementRef<T> = o
        if (elementKind != that.elementKind) return false
        return if (elementId != that.elementId) false else elementRevision == that.elementRevision
    }

    override fun hashCode(): Int {
        var result = if (elementKind != null) elementKind.hashCode() else 0
        result = 31 * result + if (elementId != null) elementId.hashCode() else 0
        result = 31 * result + if (elementRevision != null) elementRevision.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "{" +
                "\"Kind\":" + (if (elementKind == null) "null" else elementKind) + ", " +
                "\"id\":" + (if (elementId == null) "null" else "\"" + elementId + "\"") + ", " +
                "\"version\":" + (if (version == null) "null" else version) + ", " +
                "\"name\":" + (if (name == null) "null" else "\"" + name + "\"") + ", " +
                "}"
    }
}
