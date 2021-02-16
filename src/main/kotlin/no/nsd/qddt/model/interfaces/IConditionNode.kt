package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.classes.ConditionKind
import no.nsd.qddt.model.classes.Version
import java.util.*

/**
 * @author Stig Norland
 */
interface IConditionNode {
   var id: UUID
   var version: Version
   var name: String
   var condition: String?
   var conditionKind: ConditionKind
   var classKind: String
}
