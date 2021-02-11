package no.nsd.qddt.domain.agency

import java.sql.Timestamp
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Version

@Entity
class Agency (
    @Id  @GeneratedValue
    val id:UUID,
    var name: String,
    @Version
    val modified : Timestamp,
    var xmlLang:String
    ): Comparable<Agency> {
    override fun compareTo(other: Agency): Int {
        val i = this.id.compareTo(other.id)
        return when {
            i != 0 -> i
            else -> modified.compareTo(other.modified)
        }
    }
}
