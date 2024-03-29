package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import no.nsd.qddt.config.exception.StackTraceFilter
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.model.interfaces.ILabel
import no.nsd.qddt.model.interfaces.IParentRef
import org.hibernate.Hibernate
import org.hibernate.envers.AuditJoinTable
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import java.util.*
import javax.persistence.*

@Audited
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CLASS_KIND")
@JsonPropertyOrder(alphabetic = true, value = ["id","name","label","description"])
@Table(name = "CONCEPT_HIERARCHY")
abstract class ConceptHierarchy (

    @Column(length = 20000)
    var description: String=""

) : AbstractEntityAudit(), IArchived, ILabel, IParentRef {

    override var label: String = ""
        get() {
            return field ?: name
        }

//    @Embedded
//    override var basedOn: UriId = UriId()

    @Column(name = "parent_id", nullable = false,  insertable = false, updatable = false)
    var parentId: UUID? = null

    abstract var parent: ConceptHierarchy

    abstract var children: MutableList<ConceptHierarchy>

    abstract override var parentRef: IParentRef?

    fun childrenAdd(entity: ConceptHierarchy): ConceptHierarchy {
        entity.parent = this
        children.add(children.size, entity)
        super.changeKind = IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
        super.changeComment =  String.format("Added [${entity.name}]")
        return entity
    }

    fun childrenRemove(entity: ConceptHierarchy) {
        if (children.remove(entity)) {
            this.changeKind = IBasedOn.ChangeKind.UPDATED_CHILD
            this.changeComment = "Removed [${entity.name}]"
        } else
            logger.debug("entity not found, nothing to do")
    }

//    fun getModified() : Long {
//        return super.modified?.time ?: 0
//    }

    @ManyToMany
    @JoinTable(
        name = "concept_hierarchy_authors",
        joinColumns = [JoinColumn(name = "parent_id")],
        inverseJoinColumns = [JoinColumn(name = "authorId")]
    )
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @AuditJoinTable
    var authors: MutableSet<Author> = mutableSetOf()

    override var isArchived = false
        set(value) {
            try {
                field = value
                if (value) {
                    changeKind = IBasedOn.ChangeKind.ARCHIVED

                    if (Hibernate.isInitialized(children))
                        logger.debug("Children isInitialized. ")
                    else
                        Hibernate.initialize(children)

                    children.forEach { with(it as IArchived) { if (!it.isArchived) it.isArchived = true } }
                }
            } catch (ex: Exception) {
                logger.error("setArchived", ex)
                StackTraceFilter.filter(ex.stackTrace).stream()
                    .map { a -> a.toString() }
                    .forEach(logger::info)
            }
        }

}
