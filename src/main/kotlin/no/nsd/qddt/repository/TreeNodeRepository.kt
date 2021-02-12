package no.nsd.qddt.repository

import no.nsd.qddt.model.TreeNode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author Stig Norland
 */
@Repository
interface TreeNodeRepository : JpaRepository<TreeNode<*>, UUID>
