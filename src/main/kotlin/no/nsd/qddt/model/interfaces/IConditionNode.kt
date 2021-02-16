package no.nsd.qddt.model.interfaces

/**
 * @author Stig Norland
 */
interface IConditionNode {
   var id: UUID?
   var version: Version?
   var name: String?
   var conditionKind: ConditionKind?
   var classKind: String?
   var condition: String?
}
