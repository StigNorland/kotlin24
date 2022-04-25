package no.nsd.qddt.model.classes

import java.util.UUID

/**
 * @author Stig Norland
 */
data class ElementOrder(
      var uuid: UUID?=null,
      var index: Int = 0
): java.io.Serializable

