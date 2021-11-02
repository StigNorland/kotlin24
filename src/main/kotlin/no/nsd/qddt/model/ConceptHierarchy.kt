package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nsd.qddt.config.exception.StackTraceFilter
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.model.interfaces.IHaveChilden
import no.nsd.qddt.model.interfaces.IHaveParent
import org.hibernate.Hibernate
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import javax.persistence.*

@Audited
@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CLASS_KIND")
@Table(name = "CONCEPT_HIERARCHY")
abstract class ConceptHierarchy(

    @Column(length = 20000)
    var description: String=""

) : AbstractEntityAudit(), IArchived, IHaveChilden<ConceptHierarchy>, IHaveParent<ConceptHierarchy> {

    var label: String? = null
        get() { return field?:name }


    @Column(insertable = false, updatable = false)
    override var parentIdx: Int? = null

    @JsonIgnore
    @ManyToOne
//    @JoinColumn(insertable = false, updatable = false )
    override var parent: ConceptHierarchy? = null

    @OrderColumn(name = "parentIdx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
    @OneToMany(mappedBy = "parent")
    override var children: MutableList<ConceptHierarchy> = mutableListOf()


//    override fun addChild(entity: ConceptHierarchy): ConceptHierarchy {
//        children.add(entity)
//        entity.parent = this
//        changeKind = IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
//        changeComment = String.format("{} [ {} ] added", entity.classKind, entity.name)
//        return entity
//    }

    @ManyToMany
    @JoinTable(name = "concept_hierarchy_authors",
        joinColumns = [JoinColumn(name = "parentId")],
        inverseJoinColumns = [JoinColumn(name = "authorId")])
    @Audited(targetAuditMode =  RelationTargetAuditMode.NOT_AUDITED)
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
