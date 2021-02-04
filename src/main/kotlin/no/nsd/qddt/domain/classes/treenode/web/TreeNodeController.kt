package no.nsd.qddt.domain.classes.treenode.web

import no.nsd.qddt.domain.classes.treenode.TreeNode
import no.nsd.qddt.domain.classes.treenode.TreeNodeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
@RestController
@RequestMapping("/treenode")
class TreeNodeController @Autowired constructor(private val service: TreeNodeService) {
    @GetMapping(value = ["{id}"])
    operator fun get(@PathVariable("id") id: UUID): TreeNode<*> {
        return service.findOne(id)
    }

    @PutMapping(value = [""])
    fun update(@RequestBody treeNode: TreeNode<*>): TreeNode<*> {
        return service.save(treeNode)
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = [""])
    fun create(@RequestBody treeNode: TreeNode<*>): TreeNode<*> {
        return service.save(treeNode)
    }

    @DeleteMapping(value = ["/{id}"])
    fun delete(@PathVariable("id") id: UUID) {
        service.delete(id)
    }
}
