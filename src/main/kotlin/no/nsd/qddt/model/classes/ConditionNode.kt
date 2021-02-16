package no.nsd.qddt.model.classes

import no.nsd.qddt.model.interfaces.IWebMenuPreview
import java.util.*

/**
 * @author Stig Norland
 */
class ConditionNode(override var id: UUID, override var name: String, override var version: Version) : IWebMenuPreview {
    var conditionKind: ConditionKind? = null
    var classKind: String? = null
    var condition: String? = null


    constructor(instance: IConditionNode) {
        id =instance.id
        name = instance.name
        classKind = instance.classKind
        conditionKind instance.conditionKind
        condition = instance.condition
    }



}
