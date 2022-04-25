package no.nsd.qddt.model.classes

import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.ConditionKind
import no.nsd.qddt.model.interfaces.IConditionNode
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import java.util.*
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * @author Stig Norland
 */
class ConditionNode(@Id @GeneratedValue override var id: UUID?=null, override var name: String="", override var version: Version= Version()) : IWebMenuPreview {
    var conditionKind: ConditionKind? = null
    var classKind: String? = null
    var condition: String? = null


    constructor(instance: IConditionNode):this(instance.id,instance.name,instance.version)  {
        classKind = instance.classKind
        conditionKind = instance.conditionKind
        condition = instance.condition
    }



}
