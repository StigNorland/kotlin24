package no.nsd.qddt.controller

import no.nsd.qddt.model.TreeNode
import no.nsd.qddt.repository.TreeNodeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author Stig Norland
 */
@RestController
@RequestMapping("/treenode")
class TreeNodeController @Autowired constructor(private val repository: TreeNodeRepository) {
    @GetMapping(value = ["{id}"])
    operator fun get(@PathVariable("id") id: UUID): Optional<TreeNode<*>> {
        return repository.findById(id)
    }

    @PutMapping(value = [""])
    fun update(@RequestBody treeNode: TreeNode<*>): TreeNode<*> {
        return repository.save(treeNode)
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = [""])
    fun create(@RequestBody treeNode: TreeNode<*>): TreeNode<*> {
        return repository.save(treeNode)
    }

    @DeleteMapping(value = ["/{id}"])
    fun delete(@PathVariable("id") id: UUID) {
        repository.findById(id).ifPresent{
            repository.delete(it)
        }
    }
}
