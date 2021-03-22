package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.model.classes.AbstractElementRef
import no.nsd.qddt.model.classes.TreeNodeIter
import no.nsd.qddt.model.interfaces.IDomainObject
import org.hibernate.envers.Audited
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Audited
@Entity
class TreeNode<T : IDomainObject> : AbstractElementRef<T>, Iterable<TreeNode<T>> {
    @Id @GeneratedValue
    @Column(updatable = false, nullable = false)
    lateinit var id: UUID

    @ManyToOne( targetEntity = TreeNode::class)
    @JsonBackReference(value = "parentRef")
    var parent: TreeNode<T>? = null

    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.EAGER,
        targetEntity = TreeNode::class,
        cascade = [CascadeType.MERGE, CascadeType.REMOVE]
    )
    var children: MutableList<TreeNode<T>> = LinkedList()

    @JsonIgnore
    @Transient
    private var elementsIndex: MutableList<TreeNode<T>> = LinkedList()

    constructor() : super(null)
    constructor(data: T) : super(data) {
        elementsIndex.add(element = this)
    }


    // this should make Hibernate fetch children
    fun isLeaf() = children.size == 0

    val level: Int
        get() = parent?.level?:-1 +1

    fun addChild(child: T): TreeNode<T> {
        val childNode = TreeNode(child)
        childNode.parent = this
        children.add(childNode)
        registerChildForSearch(childNode)
        return childNode
    }

    fun checkInNodes() {
        children.forEach { 
            it.parent = this
            it.checkInNodes()
        }
    }

    private fun registerChildForSearch(node: TreeNode<T>) {
        elementsIndex.add(node)
        parent?.registerChildForSearch(node)
    }

    fun findTreeNode(cmp: Comparable<T>): TreeNode<T>? {
        for (element in elementsIndex) {
            val elData = element.element
            if (elData?.let { cmp.compareTo(it) } == 0) return element
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
