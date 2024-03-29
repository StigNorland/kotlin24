package no.nsd.qddt.model.classes

import no.nsd.qddt.model.TreeNode
import no.nsd.qddt.model.interfaces.IDomainObject

/**
 * @author Stig Norland
 * https://github.com/gt4dev/yet-another-tree-structure/blob/master/java/src/com/tree/TreeNodeIter.java
 */
class TreeNodeIter<T : IDomainObject>(private val treeNode: TreeNode<T>) : MutableIterator<TreeNode<T>> {
    internal enum class ProcessStages {
        ProcessParent, ProcessChildCurNode, ProcessChildSubNode
    }

    private var doNext: ProcessStages?
    private lateinit var next: TreeNode<T>
    private val childrenCurNodeIter: Iterator<TreeNode<T>>
    private var childrenSubNodeIter: Iterator<TreeNode<T>>? = null
    override fun hasNext(): Boolean {
        if (doNext == ProcessStages.ProcessParent) {
            next = treeNode
            doNext = ProcessStages.ProcessChildCurNode
            return true
        }
        if (doNext == ProcessStages.ProcessChildCurNode) {
            return if (childrenCurNodeIter.hasNext()) {
                val childDirect = childrenCurNodeIter.next()
                childrenSubNodeIter = childDirect.iterator()
                doNext = ProcessStages.ProcessChildSubNode
                hasNext()
            } else {
                doNext = null
                false
            }
        }
        return if (doNext == ProcessStages.ProcessChildSubNode) {
            if (childrenSubNodeIter!!.hasNext()) {
                next = childrenSubNodeIter!!.next()
                true
            } else {
                doNext = ProcessStages.ProcessChildCurNode
                hasNext()
            }
        } else false
    }

    override fun next(): TreeNode<T> {
        return next
    }

    override fun remove() {
        throw UnsupportedOperationException()
    }

    init {
        doNext = ProcessStages.ProcessParent
        childrenCurNodeIter = treeNode.children.iterator()
    }
}
