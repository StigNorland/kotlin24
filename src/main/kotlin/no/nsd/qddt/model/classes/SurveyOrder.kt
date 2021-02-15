package no.nsd.qddt.model.classes

import java.util.UUID

/**
 * @author Stig Norland
 */
class SurveyOrder {
    var uuid: UUID? = null
    var index: Long? = null
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as SurveyOrder
        if (if (uuid != null) uuid != that.uuid else that.uuid != null) return false
        return if (index != null) index == that.index else that.index == null
    }

    override fun hashCode(): Int {
        var result = if (uuid != null) uuid.hashCode() else 0
        result = 31 * result + if (index != null) index.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "{\"_class\":\"SurveyOrder\", " +
                "\"id\":" + (if (uuid == null) "null" else uuid) + ", " +
                "\"index\":" + (if (index == null) "null" else "\"" + index + "\"") +
                "}"
    }
}
