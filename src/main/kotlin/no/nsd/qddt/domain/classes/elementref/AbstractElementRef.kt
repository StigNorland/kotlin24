package no.nsd.qddt.domain.classes.elementref

/**
 * @author Stig Norland
 */
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.domain.classes.interfaces.IElementRef
import no.nsd.qddt.domain.classes.interfaces.IWebMenuPreview
import no.nsd.qddt.domain.classes.interfaces.Version
import no.nsd.qddt.domain.controlconstruct.pojo.ConditionConstruct
import no.nsd.qddt.domain.controlconstruct.pojo.QuestionConstruct
import no.nsd.qddt.domain.controlconstruct.pojo.StatementItem
import no.nsd.qddt.domain.questionitem.QuestionItem
import no.nsd.qddt.utils.StringTool
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@MappedSuperclass
abstract class AbstractElementRef<T : IWebMenuPreview?>  (
     elementKind: ElementKind,
     elementId: UUID,
     elementRevision: Int?
) : IElementRef<T> {
    @Enumerated(EnumType.STRING)
    override lateinit var elementKind: ElementKind

    @Type(type = "pg-uuid")
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
            elementRevision = value.revision
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

    private fun setValues(): AbstractElementRef<T> {
        if (StringTool.IsNullOrEmpty(name)) {
            when (element) {
                is QuestionItem -> name =
                    element!!.name + " ➫ " + (element as QuestionItem?)!!.question
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
        version = element!!.version
        return this
    }

}
