package no.nsd.qddt.classes.elementref

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.domain.controlconstruct.pojo.ConditionConstruct
import java.util.function.Consumer
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Audited
@Entity
@Table(name = "ELEMENT_REF_NODE")
@AttributeOverride(name = "name", column = Column(name = "element_name", length = 1500))
class ElementRefNode<T : AbstractEntityAudit?> : AbstractElementRef<T>, Iterable<ElementRefNode<T>?> {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ElementRefNode::class)
    @JsonBackReference(value = "parentRef")
    var parent: ElementRefNode<T>? = null

    // in the @OrderColumn annotation on the referencing entity.
    @Column(name = "parent_idx", insertable = false, updatable = false)
    private val parentIdx = 0

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

    constructor() {}
    constructor(data: T) {
        element = data
        children = LinkedList<ElementRefNode<T>>()
        elementsIndex = LinkedList<ElementRefNode<T>>()
        elementsIndex!!.add(this)
    }

    override var name: String?
        get() = super.name
    val isRoot: Boolean
        get() = parent == null

    // this should make Hibernate fetch children
    val isLeaf: Boolean
        get() = children!!.size == 0
    val level: Int
        get() = if (isRoot) 0 else parent!!.level + 1

    fun getId(): UUID? {
        return id
    }

    fun getChildren(): List<ElementRefNode<T>>? {
        return children
    }

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
        if (parent != null) parent.registerChildForSearch(node)
    }

    fun findTreeNode(cmp: Comparable<T>): ElementRefNode<T>? {
        for (element in elementsIndex!!) {
            val elData = element.element
            if (cmp.compareTo(elData) == 0) return element
        }
        return null
    }

    override fun iterator(): MutableIterator<ElementRefNode<T>> {
        return ElementRefNodeIter<T>(this)
    }

    override fun setValues(): AbstractElementRef<T> {
        if (getElement() == null) return this else if (element is StatementItem) name =
            getElement().name + " ➫ " + (element as StatementItem).getStatement() else if (element is ConditionConstruct) {
            println("ignorerer set value")
        } else if (element is QuestionConstruct) {
            //
        } else setVersion(getElement().version)
        if (elementKind == null) elementKind = ElementKind.Companion.getEnum(element.class.simpleName)
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as ElementRefNode<*>
        return id == that.id
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun toString(): String {
        return if (element != null) element.toString() else "[data null]"
    }
}
