package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.embedded.Version as EmbeddedVersion

import java.util.*

/**
 * @author Stig Norland
 */
interface IWebMenuPreview {
    var id: UUID
    var name: String
    var version: EmbeddedVersion
}
