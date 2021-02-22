package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder

/**
 * @author Stig Norland
 */
interface IXmlBuilder {
    fun xmlBuilder(): AbstractXmlBuilder?
}
