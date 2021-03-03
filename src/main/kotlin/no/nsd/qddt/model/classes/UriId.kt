package no.nsd.qddt.model.classes

import org.hibernate.envers.Audited
import java.io.Serializable
import java.util.*
import javax.persistence.*


/**
 * @author Stig Norland
 */
@Embeddable
class UriId: Comparable<UriId> , Serializable {

    lateinit var id: UUID

    var rev: Int? = null

    fun fromString(uri: String) : UriId {
        val parts = uri.split(":")
        return UriId().apply { 
            id = UUID.fromString(parts[0])
            if (parts.size==2)
                rev = parts[1].toInt()
        }
    }

    override fun toString(): String {
        if (rev != null)
            return String.format("{}:{}", id, rev)
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

}