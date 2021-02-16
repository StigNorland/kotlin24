package no.nsd.qddt.model.classes

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import java.util.*
//import no.nsd.qddt.domain.controlconstruct.pojo.ConditionConstruct
import java.util.function.Consumer
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Audited
@Entity
@Table(name = "ELEMENT_REF_NODE")
@AttributeOverride(name = "name", column = Column(name = "element_name", length = 1500))
class ElementRefNode<T : AbstractEntityAudit> : AbstractElementRef<T>, Iterable<ElementRefNode<T>?> {
    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    lateinit var id: UUID

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ElementRefNode::class)
    @JsonBackReference(value = "parentRef")
    var parent: ElementRefNode<T>? = null

    // in the @OrderColumn annotation on the referencing entity.
    @Column(name = "parent_idx", insertable = false, updatable = false)
    private var parentIdx = 0

    @OrderColumn(name = "parent_idx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.EAGER,
        targetEntity = ElementRefNode::class,
        orphanRemoval = true,
        cascade = [CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE]
    )
    var children: MutableList<ElementRefNode<T>>? = null

    @JsonIgnore
    @Transient
    private var elementsIndex: MutableList<ElementRefNode<T>>? = null

    constructor() : super(null)
    constructor(data: T) : super(data) {
        element = data
        children = LinkedList<ElementRefNode<T>>()
        elementsIndex = LinkedList<ElementRefNode<T>>()
        elementsIndex!!.add(this)
    }

    val isRoot: Boolean
        get() = parent == null

    // this should make Hibernate fetch children
    val isLeaf: Boolean
        get() = children!!.size == 0
    val level: Int
        get() = if (isRoot) 0 else parent!!.level + 1




    fun addChild(child: T): ElementRefNode<T> {
        val childNode = ElementRefNode(child)
        childNode.parent = this
        children!!.add(childNode)
        registerChildForSearch(childNode)
        return childNode
    }

    fun checkInNodes() {
        children!!.forEach(Consumer { c: ElementRefNode<T> ->
            c.parent = this
            c.checkInNodes()
        })
    }

    private fun registerChildForSearch(node: ElementRefNode<T>) {
        elementsIndex!!.add(node)
        parent?.registerChildForSearch(node)
    }

    fun findTreeNode(cmp: Comparable<T>): ElementRefNode<T>? {
        for (element in elementsIndex!!) {
            val elData = element.element
            if (elData?.let { cmp.compareTo(it) } == 0) return element
        }
        return null
    }

    override fun iterator(): ElementRefNodeIter<T> {
        return ElementRefNodeIter(this)
    }

    override fun setValues(): AbstractElementRef<T> {
        if (element == null)
            return this
//        else if (element is StatementItem)
//            name = element!!.name + " ➫ " + (element as StatementItem).getStatement() else if (element is ConditionConstruct) {
//            println("ignorerer set value")
//        } else if (element is QuestionConstruct) { }
    else version = element!!.version
        return this
    }

}
