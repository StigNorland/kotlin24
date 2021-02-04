package no.nsd.qddt.domain.classes.treenode

import no.nsd.qddt.domain.classes.exception.ResourceNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * @author Stig Norland
 */
@Service("treeNodeService")
class TreeNodeServiceImpl @Autowired constructor(private val treeNodeRepository: TreeNodeRepository) : TreeNodeService {
    @Transactional(readOnly = true)
    override fun count(): Long {
        return treeNodeRepository.count()
    }

    @Transactional(readOnly = true)
    override fun exists(uuid: UUID): Boolean {
        return treeNodeRepository.existsById(uuid)
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR') and hasPermission(#instance,'AGENCY')")
    override fun <S : TreeNode<*>?> save(instance: S): S {
        return treeNodeRepository.save(instance)
    }

    @Transactional(readOnly = true)
    override fun findOne(uuid: UUID): TreeNode<*>? {
        return treeNodeRepository.findById(uuid).orElseThrow { ResourceNotFoundException(uuid, TreeNode::class.java) }
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR')")
    override fun delete(uuid: UUID) {
        treeNodeRepository.deleteById(uuid)
    }

    protected fun prePersistProcessing(instance: TreeNode<*>): TreeNode<*> {
//        instance.addChild(  )
        return instance
    }

    protected fun postLoadProcessing(instance: TreeNode<*>): TreeNode<*> {
        return instance
    }
}
