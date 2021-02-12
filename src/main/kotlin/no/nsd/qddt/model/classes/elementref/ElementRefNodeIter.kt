package no.nsd.qddt.model.classes.elementref

import no.nsd.qddt.model.classes.AbstractEntityAudit


/**
 * @author Stig Norland
 */
class ElementRefNodeIter<T : AbstractEntityAudit>
    (private val treeNode: ElementRefNode<T>) :  MutableIterator<ElementRefNode<T>?> {

    internal enum class ProcessStages {
        ProcessParent, ProcessChildCurNode, ProcessChildSubNode
    }

    private var doNext: ProcessStages?
    private var next: ElementRefNode<T>? = null
    private val childrenCurNodeIter: Iterator<ElementRefNode<T>>
    private var childrenSubNodeIter: Iterator<ElementRefNode<T>>? = null

    override fun hasNext(): Boolean {
        if (doNext == ProcessStages.ProcessParent) {
            next = treeNode
            doNext = ProcessStages.ProcessChildCurNode
            return true
        }
        if (doNext == ProcessStages.ProcessChildCurNode) {
            return if (childrenCurNodeIter.hasNext()) {
                val childDirect = childrenCurNodeIter.next()
                childrenSubNodeIter = childDirect.children?.iterator()
                doNext = ProcessStages.ProcessChildSubNode
                hasNext()
            } else {
                doNext = null
                false
            }
        }
        return if (doNext == ProcessStages.ProcessChildSubNode) {
            if (childrenSubNodeIter?.hasNext() == true) {
                next = childrenSubNodeIter?.next()
                true
            } else {
                next = null
                doNext = ProcessStages.ProcessChildCurNode
                hasNext()
            }
        } else false
    }

    override fun next(): ElementRefNode<T>? {
        return next
    }

    override fun remove() {
        throw UnsupportedOperationException()
    }

    init {
        doNext = ProcessStages.ProcessParent
        childrenCurNodeIter = treeNode.children!!.iterator()
    }
}
