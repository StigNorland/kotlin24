package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.enums.ConditionKind
import no.nsd.qddt.model.embedded.Version
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
