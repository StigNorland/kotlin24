package no.nsd.qddt.model.classes

/**
 * @author Stig Norland
 */
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.ConditionConstruct
import no.nsd.qddt.model.QuestionConstruct
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.StatementItem
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IElementRef
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import no.nsd.qddt.utils.StringTool
import java.util.*
import javax.persistence.*

@MappedSuperclass
abstract class AbstractElementRef<T : IWebMenuPreview> : IElementRef<T> {
    constructor(entity: T?) {
        entity?.let{
            element = it
        }
    }
    constructor(elementKind: ElementKind, elementId: UUID, elementRevision: Int?) {
        this.elementKind = elementKind
        this.elementId = elementId
        this.elementRevision = elementRevision
    }

    @Enumerated(EnumType.STRING)
    override lateinit var elementKind: ElementKind


    override lateinit var elementId: UUID

    @Column(name = "element_revision")
    override var elementRevision: Int? = 0

    @Column(name = "element_name", length = 500)
    override var name: String? = null

    override var version: Version
        get() = Version(major, minor, elementRevision?:0, versionLabel?:"")
        set(value) {
            major = value.major
            minor = value.minor
            versionLabel = value.versionLabel
            elementRevision = value.rev
        }

    @Column(name = "element_major")
    private var major: Int = 1

    @Column(name = "element_minor")
    private var minor: Int = 0

    @Column(name = "element_version_label")
    private var versionLabel: String? = null

    @Transient
    @JsonSerialize
    @JsonDeserialize
    override var element: T? = null
    set(value) {
        field = value
        setValues()
    }

    protected fun setValues(): AbstractElementRef<T> {
        if (StringTool.IsNullOrEmpty(name)) {
            when (element) {
                is QuestionItem -> (element!!.name + " ➫ " + (element as QuestionItem?)!!.question).also { name = it }
                 is StatementItem -> name =
                     element!!.name + " ➫ " + (element as StatementItem?)!!.statement
                 is ConditionConstruct -> {
                     name = element!!.name + " ➫ " + (element as ConditionConstruct?)!!.condition
                     println(
                         ElementKind.getEnum(element!!::class.simpleName)
                             .toString() + " - ConditionConstruct- name set"
                     )
                 }
                 is QuestionConstruct -> {
                     println(
                         ElementKind.getEnum(element!!::class.simpleName)
                             .toString() + " - QuestionConstruct name not set"
                     )
                 }
                else -> println(ElementKind.getEnum(element!!::class.simpleName).toString() + " - set name")
            }
            name = element!!.name
        }
        element?.also {
            version = it.version
            name = it.name
            elementId = it.id
        }
        return this
    }

}
