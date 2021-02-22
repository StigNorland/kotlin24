package no.nsd.qddt.model.interfaces

import java.util.*
import no.nsd.qddt.model.embedded.Version as EmbeddedVersion

/**
 * @author Stig Norland
 */
interface IWebMenuPreview {
    var id: UUID
    var name: String
    var version: EmbeddedVersion
}
