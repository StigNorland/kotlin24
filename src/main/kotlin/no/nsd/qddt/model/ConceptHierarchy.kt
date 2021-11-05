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
import java.util.*
import javax.persistence.*

@Audited
@Entity
//@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CLASS_KIND")
@Table(name = "CONCEPT_HIERARCHY")
abstract class ConceptHierarchy(

    @Column(length = 20000)
    var description: String=""

) : AbstractEntityAudit(), IArchived {

    var label: String? = null
        get() { return field?:name }



//    @JsonIgnore
//    @ManyToOne
////    @JoinColumn(insertable = false, updatable = false )
//    var parent: ConceptHierarchy? = null



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



}
