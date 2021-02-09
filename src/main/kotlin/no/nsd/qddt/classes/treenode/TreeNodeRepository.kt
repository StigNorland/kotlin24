package no.nsd.qddt.classes.treenode

import no.nsd.qddt.classes.interfaces.BaseRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author Stig Norland
 */
@Repository
interface TreeNodeRepository : BaseRepository<TreeNode<*>?, UUID?>
