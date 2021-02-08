package no.nsd.qddt.domain.classes.interfaces

import no.nsd.qddt.domain.classes.interfaces.Version as EmbeddedVersion

import java.util.*

/**
 * @author Stig Norland
 */
interface IWebMenuPreview {
    val id: UUID
    var name: String
    var version: EmbeddedVersion
}
