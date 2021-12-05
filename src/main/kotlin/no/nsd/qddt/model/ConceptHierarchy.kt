package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.interfaces.IArchived
import org.hibernate.envers.AuditJoinTable
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import java.util.*
import javax.persistence.*

@Audited
@Entity
@Cacheable
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CLASS_KIND")
@Table(name = "CONCEPT_HIERARCHY")
abstract class ConceptHierarchy(

    @Column(length = 20000)
    var description: String=""

) : AbstractEntityAudit(), IArchived {

    @JsonIgnore
    @Column(insertable = false, updatable = false)
    protected var parentId: UUID? = null


    var label: String? = null
        get() { return field?:name }

    fun getModified() : Long {
        return super.modified!!.time
    }



    @ManyToMany
    @JoinTable(name = "concept_hierarchy_authors",
        joinColumns = [JoinColumn(name = "parent_id")],
        inverseJoinColumns = [JoinColumn(name = "authorId")])
    @Audited(targetAuditMode =  RelationTargetAuditMode.NOT_AUDITED)
    @AuditJoinTable
    var authors: MutableSet<Author> = mutableSetOf()



}
