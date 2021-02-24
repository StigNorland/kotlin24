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

    @JsonIgnore
//    @JsonBackReference(value = "agentRefSurvey")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agency", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    var surveyPrograms: MutableList<SurveyProgram> = mutableListOf()


    @JsonIgnore
    @JsonBackReference(value = "agentRefUser")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agency", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    var users: MutableList<User> = mutableListOf()


    override fun compareTo(other: Agency): Int {
        val i = this.id.compareTo(other.id)
        return when {
            i != 0 -> i
            else -> modified.compareTo(other.modified)
        }
    }
}
