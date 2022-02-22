package no.nsd.qddt.model.classes

import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable


/**
 * @author Stig Norland
 */
@Embeddable
data class UriId(
    var rev: Int? = null
): Comparable<UriId> ,Serializable {

    lateinit var id: UUID

    override fun toString(): String {
        return if (rev != null) "$id:$rev" else id.toString()
    }

    override fun compareTo(other: UriId): Int {
        return try
        {
            val i = id.compareTo(other.id)
            if (i != 0) i else (rev?:0).compareTo(other.rev?:0)
        }
        catch (nfe:NumberFormatException) {
          id.compareTo(id)
        }
    }

//    override fun convert(source: Serializable): UriId? {
//        return fromAny(source)
//    }


    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (rev?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UriId

        if (id != other.id) return false
        if (rev != other.rev) return false

        return true
    }

    companion object {
        fun fromAny(source: Any): UriId {
            val parts =  source.toString().split(":")
            return UriId().apply {
                id = UUID.fromString(parts[0])
                if (parts.size==2 && parts[1].toInt() > 0)
                    rev = parts[1].toInt()
            }

        }
    }

}
