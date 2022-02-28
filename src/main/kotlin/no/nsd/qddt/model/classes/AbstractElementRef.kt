package no.nsd.qddt.model.classes

/**
 * @author Stig Norland
 */
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.ConditionConstruct
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.StatementItem
import no.nsd.qddt.model.embedded.UriId
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IElementRef
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import org.hibernate.envers.Audited
import javax.persistence.*

@Audited
@MappedSuperclass
abstract class AbstractElementRef<T : IWebMenuPreview>() : IElementRef<T> {

    constructor(entity: T?) : this() {
        entity?.let{
            element = it
        }
    }
    constructor(elementKind: ElementKind, uriId: UriId) : this() {
        this.elementKind = elementKind
        this.uri = uriId
    }

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
        AttributeOverride(name = "major",       column = Column(name = "element_major")),
        AttributeOverride(name = "minor",       column = Column(name = "element_minor")),
        AttributeOverride(name = "rev",         column = Column(name = "element_revision")),
        AttributeOverride(name = "versionLabel",column = Column(name = "element_version_label"))
    )
    @Transient
    override var version: Version = Version()

   
    @Transient
    @JsonSerialize
    override var element: T? = null
        set(value) {
            field = value?.also { item ->
                uri = UriId().also {
                    it.id = item.id!!
                    it.rev = item.version.rev
                }
                version = item.version
                when (item) {
                    is QuestionItem -> (item.name + " ➫ " + item.question).also { name = it }
                    is StatementItem -> (item.name + " ➫ " + item.statement).also { name = it }
                    is ConditionConstruct -> (item.name + " ➫ " + item.condition).also { name = it }
                    // is QuestionConstruct -> name = it
                    else -> name = item.name
                }
            }
        }

}
