package no.nsd.qddt.model.classes

import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable


/**
 * @author Stig Norland
 */
@Embeddable
class UriId: Comparable<UriId> , Serializable {

    lateinit var id: UUID

    var rev: Int? = null

    override fun toString(): String {
        if (rev != null)
            return "$id:$rev"
        return id.toString()
    }

    override fun compareTo(other: UriId):Int {
        return try
        {
            val i = id.compareTo(other.id)
            return if (i != 0) i else rev?:0.compareTo(other.rev?:0)
        }
        catch (nfe:NumberFormatException) {
          id.compareTo(id)
        }
    }

    companion object {
        fun fromString(uri: String) : UriId {
            val parts = uri.split(":")
            return UriId().apply {
                id = UUID.fromString(parts[0])
                if (parts.size==2)
                    rev = parts[1].toInt()
            }
        }

    }

}
