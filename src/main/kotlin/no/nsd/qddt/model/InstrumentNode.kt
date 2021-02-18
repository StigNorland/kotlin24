package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.model.classes.AbstractElementRef
import no.nsd.qddt.model.classes.ConditionNode
import no.nsd.qddt.model.embedded.Parameter
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.IConditionNode
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.function.Consumer
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Audited
@Entity
@Table(name = "INSTRUMENT_NODE")
@AttributeOverride(name = "name", column = Column(name = "element_name", length = 1500))
class InstrumentNode<T : ControlConstruct> : AbstractElementRef<T>, Iterable<InstrumentNode<T>> {
    @Id @GeneratedValue
    @Column(updatable = false, nullable = false)
    lateinit var id: UUID

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = InstrumentNode::class)
    @JsonBackReference(value = "parentRef")
    var parent: InstrumentNode<T>? = null

    // in the @OrderColumn annotation on the referencing entity.
    @Column(name = "parent_idx", insertable = false, updatable = false)
    private var parentIdx: Int = -1

    @OrderColumn(name = "parent_idx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.EAGER,
        targetEntity = InstrumentNode::class,
        orphanRemoval = true,
        cascade = [CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE]
    )
    var children: MutableList<InstrumentNode<T>> = mutableListOf()

    @JsonIgnore
    @Transient
    private var elementsIndex: MutableList<InstrumentNode<T>> = mutableListOf()

    @OrderColumn(name = "node_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "INSTRUMENT_PARAMETER",
        joinColumns = [JoinColumn(name = "node_id", referencedColumnName = "id")]
    )
    private var parameters: MutableList<Parameter> = mutableListOf()

    constructor(data: T) : super(data) {
        elementsIndex.add(this)
    }

    fun addParameter(parameter: Parameter) {
        if (parameters.stream()
                .noneMatch { p: Parameter -> p.name == parameter.name && p.parameterKind == parameter.parameterKind }
        ) parameters.add(parameter)
    }

    fun clearInParameters() {
        parameters.removeIf { p: Parameter -> p.parameterKind == "IN" }
    }

    val isRoot: Boolean
        get() = parent == null

    // this should make Hibernate fetch children
    val isLeaf: Boolean
        get() = children.size == 0
    val level: Int
        get() = if (isRoot) 0 else parent!!.level + 1


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
        elementsIndex.add(node)
        parent?.registerChildForSearch(node)
    }

    fun findTreeNode(cmp: Comparable<T>): InstrumentNode<T>? {
        for (element in elementsIndex) {
            val elData: T = element.element!!
            if (cmp.compareTo(elData) == 0) return element
        }
        return null
    }


    override fun iterator(): Iterator<InstrumentNode<T>> {
        return InstrumentNodeIter(this).iterator()
    }

    protected fun setValues(element: T): AbstractElementRef<T> {
        when (element) {
            is StatementItem -> name = element.name + " ➫ " + (element as StatementItem).statement
            is ConditionConstruct -> println("ignorerer set value")
            is QuestionConstruct -> println("ignorerer set value")
            is ControlConstruct -> {
                elementKind = ElementKind.getEnum(element.classKind)
                version = element.version
            }
        }
        return this
    }
}
