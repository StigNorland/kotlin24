package no.nsd.qddt.model.builder.xml

import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.enums.ElementKind

/**
 * @author Stig Norland
 */
open class XmlDDIInstanceBuilder<T : AbstractEntityAudit>(protected val instance: T) : AbstractXmlBuilder() {
    private val ddiXmlRoot =
"""
<DDIInstance 
    xmlns:g="ddi:group:3_2" 
    xmlns:d="ddi:datacollection:3_2"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:r="ddi:reusable:3_2"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:l="ddi:logicalproduct:3_2"
    xmlns:pr="ddi:ddiprofile:3_2"
    xmlns ="ddi:instance:3_2"
    xmlns:s="ddi:studyunit:3_2"
    xsi:schemaLocation="ddi:instance:3_2 http://www.ddialliance.org/Specification/DDI-Lifecycle/3.2/XMLSchema/instance.xsd">
"""
    protected val id: String
        protected get() = "null"

    override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {}

    override fun getXmlEntityRef(depth: Int): String {
        return "null"
    }

    override val xmlFragment: String
        get() = "null"
}
