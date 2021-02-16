package no.nsd.qddt.model



/**
 * @author Stig Norland
 * https://github.com/gt4dev/yet-another-tree-structure/blob/master/java/src/com/tree/TreeNodeIter.java
 */
class InstrumentNodeIter<T : ControlConstruct>(private val treeNode: InstrumentNode<T>) : MutableIterator<InstrumentNode<T>> {
    internal enum class ProcessStages {
        ProcessParent, ProcessChildCurNode, ProcessChildSubNode
    }

    private var doNext: ProcessStages?
    private var next: InstrumentNode<T>? = null
    private val childrenCurNodeIter: Iterator<InstrumentNode<T>>
    private var childrenSubNodeIter: Iterator<InstrumentNode<T>>? = null
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
                next = null
                doNext = ProcessStages.ProcessChildCurNode
                hasNext()
            }
        } else false
    }

    override fun next(): InstrumentNode<T> {
        return next!!
    }

    override fun remove() {
        throw UnsupportedOperationException()
    }

    init {
        doNext = ProcessStages.ProcessParent
        childrenCurNodeIter = treeNode.children.iterator()
    }
}
