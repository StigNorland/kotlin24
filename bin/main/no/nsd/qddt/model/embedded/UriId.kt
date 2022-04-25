package no.nsd.qddt.model.embedded

import org.hibernate.envers.Audited
import org.springframework.core.convert.converter.Converter
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable


/**
 * @author Stig Norland
 */
@Embeddable
@Audited
class UriId: Comparable<UriId> , Serializable, Converter<Serializable, UriId> {

    var id: UUID? = null

    var rev: Int? = null

//    fun isValid():Boolean = (this.id!=null)
//
//    fun isRev():Boolean =  isValid() && (this.rev != null)

    override fun toString(): String {
        if (id == null)
            return "null"
        if (rev == null)
            return "$id"
        return "$id:$rev"
    }

    override fun compareTo(other: UriId): Int {
        return try
        {
            val i = id?.compareTo(other.id)?:0
            return if (i != 0) i else (rev?:0).compareTo(other.rev?:0)
        }
        catch (nfe:NumberFormatException) {
          -1
        }
    }

    override fun convert(source: Serializable): UriId {
        return fromAny(source)
    }

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
                if (parts.isNotEmpty()) {
                    id = UUID.fromString(parts[0])
                    if (parts.size==2)
                        rev = try {
                            parts[1].toInt()
                        } catch(ex: Exception){
                            0
                        }
                }
            }

        }
    }

}
