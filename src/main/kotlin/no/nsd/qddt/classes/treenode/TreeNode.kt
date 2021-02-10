package no.nsd.qddt.classes.treenode

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.classes.elementref.AbstractElementRef
import no.nsd.qddt.classes.interfaces.IDomainObject
import org.hibernate.annotations.GenericGenerator
import org.hibernate.envers.Audited
import java.util.*
import java.util.function.Consumer
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Audited
@Entity
class TreeNode<T : IDomainObject> : AbstractElementRef<T>, Iterable<TreeNode<T>> {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column( updatable = false, nullable = false)
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = TreeNode::class)
    @JsonBackReference(value = "parentRef")
    var parent: TreeNode<T>? = null

    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.EAGER,
        targetEntity = TreeNode::class,
        cascade = [CascadeType.MERGE, CascadeType.REMOVE]
    )
    var children: MutableList<TreeNode<T>>? = null

    @JsonIgnore
    @Transient
    private var elementsIndex: MutableList<TreeNode<T>>? = null

    constructor() : super()
    constructor(data: T) {
        element = data
        children = LinkedList()
        elementsIndex = LinkedList()
        elementsIndex.add(this)
    }

    val isRoot: Boolean
        get() = parent == null

    // this should make Hibernate fetch children
    val isLeaf: Boolean
        get() = children!!.size == 0
    val level: Int
        get() = if (isRoot) 0 else parent!!.level + 1

    fun addChild(child: T): TreeNode<T> {
        val childNode = TreeNode(child)
        childNode.parent = this
        children!!.add(childNode)
        registerChildForSearch(childNode)
        return childNode
    }

    fun checkInNodes() {
        children!!.forEach(Consumer { c: TreeNode<T> ->
            c.parent = this
            c.checkInNodes()
        })
    }

    private fun registerChildForSearch(node: TreeNode<T>) {
        elementsIndex!!.add(node)
        parent?.registerChildForSearch(node)
    }

    fun findTreeNode(cmp: Comparable<T>): TreeNode<T>? {
        for (element in elementsIndex!!) {
            val elData = element.element
            if (cmp.compareTo(elData) == 0) return element
        }
        return null
    }

    override fun toString(): String {
        return if (element != null) element.toString() else "[data null]"
    }

    override fun iterator(): MutableIterator<TreeNode<T>> {
        return TreeNodeIter(this)
    }
}
