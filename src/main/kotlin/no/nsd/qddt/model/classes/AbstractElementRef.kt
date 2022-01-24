package no.nsd.qddt.model.classes

/**
 * @author Stig Norland
 */
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.ConditionConstruct
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.StatementItem
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IElementRef
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import org.hibernate.envers.Audited
import java.util.*
import javax.persistence.*

@Audited
@MappedSuperclass
abstract class AbstractElementRef<T : IWebMenuPreview> : IElementRef<T> {

    constructor()

    constructor(entity: T?) {
        entity?.let{
            element = it
        }
    }
    constructor(elementKind: ElementKind, elementId: UUID?, elementRevision: Int?) {
        this.elementKind = elementKind
        this.elementId = elementId
        this.elementRevision = elementRevision
    }

    @Enumerated(EnumType.STRING)
    override lateinit var elementKind: ElementKind

    override var elementId: UUID?=null

    @Column(name = "element_name", length = 500)
    override var name: String? = null

    @Column(name = "element_major" )
    var major: Int? = null

    @Column(name = "element_minor" )
    var minor: Int? = null

    @Column(name = "element_version_label" )
    var versionLabel: String? = null
    
    @Column(name = "element_revision" )
    override var elementRevision: Int? = null

    @AttributeOverrides(
        AttributeOverride(name = "major",       column = Column(name = "element_major")),
        AttributeOverride(name = "minor",       column = Column(name = "element_minor")),
        AttributeOverride(name = "rev",         column = Column(name = "element_revision")),
        AttributeOverride(name = "versionLabel",column = Column(name = "element_version_label"))
    )
    @Transient
    override var version: Version = Version()

   
    @Transient
    @JsonSerialize
    @JsonDeserialize
    override var element: T? = null
        set(value) {
            field = value
            value?.let {
                elementId = it.id
                version = it.version
                when (it) {
                    is QuestionItem -> (it.name + " ➫ " + it.question).also { name = it }
                    is StatementItem -> (it.name + " ➫ " + it.statement).also { name = it }
                    is ConditionConstruct -> (it.name + " ➫ " + it.condition).also { name = it }
                    // is QuestionConstruct -> name = it
                    else -> name = it.name
                }
            }
            if (value == null) {
                name = ""
            }
        }

}
