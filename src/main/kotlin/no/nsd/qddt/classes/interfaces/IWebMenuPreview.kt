package no.nsd.qddt.classes.interfaces

import no.nsd.qddt.classes.interfaces.Version as EmbeddedVersion

import java.util.*

/**
 * @author Stig Norland
 */
interface IWebMenuPreview {
    var id: UUID
    var name: String
    var version: EmbeddedVersion
}
