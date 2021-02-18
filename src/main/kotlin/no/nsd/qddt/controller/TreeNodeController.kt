package no.nsd.qddt.controller

/**
 * @author Stig Norland
 */
//@RestController
//@RequestMapping("/treenode")
//class TreeNodeController @Autowired constructor(private val repository: TreeNodeRepository) {
//    @GetMapping(value = ["{id}"])
//    operator fun get(@PathVariable("id") id: UUID): Optional<TreeNode<*>> {
//        return repository.findById(id)
//    }
//
//    @PutMapping(value = [""])
//    fun update(@RequestBody treeNode: TreeNode<*>): TreeNode<*> {
//        return repository.save(treeNode)
//    }
//
//    @ResponseStatus(value = HttpStatus.CREATED)
//    @PostMapping(value = [""])
//    fun create(@RequestBody treeNode: TreeNode<*>): TreeNode<*> {
//        return repository.save(treeNode)
//    }
//
//    @DeleteMapping(value = ["/{id}"])
//    fun delete(@PathVariable("id") id: UUID) {
//        repository.findById(id).ifPresent{
//            repository.delete(it)
//        }
//    }
//}
