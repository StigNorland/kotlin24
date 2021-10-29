package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.*
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.config.exception.StackTraceFilter
import no.nsd.qddt.model.interfaces.*
import org.hibernate.Hibernate
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import java.lang.Exception
import java.util.*
import javax.persistence.*

@Audited
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CLASS_KIND")
@Table(name = "CONCEPT_HIERARCHY")
abstract class ConceptHierarchy(

    var label: String="",

    @Column(length = 20000)
    var description: String="",

    @Column(insertable = false, updatable = false)
    override var parentId: UUID? = null

) : AbstractEntityAudit(), IArchived, IHaveChilden<ConceptHierarchy>, IHaveParent<ConceptHierarchy> {

    @Column(insertable = false, updatable = false)
    override var parentIdx: Int? = null

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="parentId", insertable = false, updatable = false )
    override var parent: ConceptHierarchy? = null

    @JsonIgnoreProperties("parent")
    @OrderColumn(name = "parentIdx")
    @AuditMappedBy(mappedBy = "parentId", positionMappedBy = "parentIdx")
    @OneToMany(mappedBy = "parentId")
    override var children: MutableList<ConceptHierarchy> = mutableListOf()


    override fun addChild(entity: ConceptHierarchy): ConceptHierarchy {
        children.add(entity)
        entity.parent = this
        changeKind = IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
        changeComment = String.format("{} [ {} ] added", entity.classKind, entity.name)
        return entity
    }

    @ManyToMany(mappedBy = "conceptReferences")
//    @JoinTable(name = "concept_hierarchy_authors",
//        joinColumns = [JoinColumn(name = "parentId")],
//        inverseJoinColumns = [JoinColumn(name = "authorId")])
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

                    children.forEach{  with (it as IArchived){ if (!it.isArchived) it.isArchived = true }}
                }
            } catch (ex: Exception) {
                logger.error("setArchived", ex)
                StackTraceFilter.filter(ex.stackTrace).stream()
                        .map { a -> a.toString() }
                        .forEach(logger::info)
            }
        }
}
