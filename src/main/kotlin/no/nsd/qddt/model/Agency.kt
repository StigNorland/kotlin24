package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Cacheable
@Entity
class Agency : Comparable<Agency> {
    @Id @GeneratedValue
    @Column(updatable = false, nullable = false)
    lateinit var id: UUID

    lateinit var name: String

    @Version
    lateinit var modified : Timestamp

    var xmlLang:String="en-GB"

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agencyId", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    var surveyPrograms: MutableList<SurveyProgram> = mutableListOf()


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agencyId", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    var users: MutableSet<User> = mutableSetOf()


    override fun compareTo(other: Agency): Int {
        val i = this.id.compareTo(other.id)
        return when {
            i != 0 -> i
            else -> modified.compareTo(other.modified)
        }
    }
}
