package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.domain.classes.elementref.AbstractElementRef
import no.nsd.qddt.model.classes.ConditionNode
import java.util.ArrayList
import java.util.function.Consumer
import java.util.function.Predicate
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Audited
@Entity
@Table(name = "INSTRUMENT_NODE")
@AttributeOverride(name = "name", column = Column(name = "element_name", length = 1500))
class InstrumentNode<T : ControlConstruct?> : AbstractElementRef<T>, Iterable<InstrumentNode<T>?> {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = InstrumentNode::class)
    @JsonBackReference(value = "parentRef")
    var parent: InstrumentNode<T>? = null

    // in the @OrderColumn annotation on the referencing entity.
    @Column(name = "parent_idx", insertable = false, updatable = false)
    private val parentIdx = 0

    @OrderColumn(name = "parent_idx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.EAGER,
        targetEntity = InstrumentNode::class,
        orphanRemoval = true,
        cascade = [CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE]
    )
    var children: MutableList<InstrumentNode<T>> = ArrayList(0)

    @JsonIgnore
    @Transient
    private var elementsIndex: MutableList<InstrumentNode<T>>? = null

    @OrderColumn(name = "node_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "INSTRUMENT_PARAMETER",
        joinColumns = [JoinColumn(name = "node_id", referencedColumnName = "id")]
    )
    private var parameters: MutableList<no.nsd.qddt.domain.instrument.pojo.Parameter> =
        ArrayList<no.nsd.qddt.domain.instrument.pojo.Parameter>()

    constructor() {}
    constructor(data: T) {
        this.element = data
        children = LinkedList<InstrumentNode<T>>()
        elementsIndex = LinkedList<InstrumentNode<T>>()
        elementsIndex!!.add(this)
    }

    fun getParameters(): List<no.nsd.qddt.domain.instrument.pojo.Parameter> {
        return parameters
    }

    fun setParameters(parameters: MutableList<no.nsd.qddt.domain.instrument.pojo.Parameter>) {
        this.parameters = parameters
    }

    fun addParameter(parameter: no.nsd.qddt.domain.instrument.pojo.Parameter) {
        if (parameters.stream()
                .noneMatch(Predicate<no.nsd.qddt.domain.instrument.pojo.Parameter> { p: no.nsd.qddt.domain.instrument.pojo.Parameter -> p.getName() == parameter.getName() && p.getParameterKind() == parameter.getParameterKind() })
        ) parameters.add(parameter)
    }

    fun clearInParameters() {
        parameters.removeIf(Predicate<no.nsd.qddt.domain.instrument.pojo.Parameter> { p: no.nsd.qddt.domain.instrument.pojo.Parameter -> p.getParameterKind() == "IN" })
    }

    val isRoot: Boolean
        get() = parent == null

    // this should make Hibernate fetch children
    val isLeaf: Boolean
        get() = children.size == 0
    val level: Int
        get() = if (isRoot) 0 else parent!!.level + 1

    fun getId(): UUID? {
        return id
    }

    fun getChildren(): List<InstrumentNode<T>> {
        return children
    }

    fun addChild(child: T): InstrumentNode<T> {
        val childNode = InstrumentNode(child)
        childNode.parent = this
        children.add(childNode)
        registerChildForSearch(childNode)
        return childNode
    }

    private fun createConditionNode(): ConditionNode? {
        try {
            return ConditionNode(javaClass.getMethod("getElement").invoke(this) as IConditionNode)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return null
    }

    fun checkInNodes() {
        children.forEach(Consumer { c: InstrumentNode<T> ->
            c.parent = this
            c.checkInNodes()
        })
    }

    private fun registerChildForSearch(node: InstrumentNode<T>) {
        elementsIndex!!.add(node)
        if (parent != null) parent.registerChildForSearch(node)
    }

    fun findTreeNode(cmp: Comparable<T>): InstrumentNode<T>? {
        for (element in elementsIndex!!) {
            val elData: T = element.element
            if (cmp.compareTo(elData) == 0) return element
        }
        return null
    }

    fun setName(name: String) {
        name = name
    }

    override fun iterator(): Iterator<InstrumentNode<T>> {
        return InstrumentNodeIter<T>(this)
    }

    protected fun setValues(): AbstractElementRef<T> {
        if (getElement() == null) return this else if (element is StatementItem) setName(
            getElement().getName().toString() + " ➫ " + (element as StatementItem).getStatement()
        ) else if (element is ConditionConstruct) {
            println("ignorerer set value")
        } else if (element is QuestionConstruct) {
            //
        } else setVersion(getElement().getVersion())
        if (this.getElementKind() == null) setElementKind(ElementKind.getEnum(element.getClass().getSimpleName()))
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as InstrumentNode<*>
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
