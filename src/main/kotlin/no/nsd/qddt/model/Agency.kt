package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
class Agency (
    @Id  @GeneratedValue
    val id:UUID,
    var name: String,
    @Version
    val modified : Timestamp,
    var xmlLang:String
    ): Comparable<Agency> {

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "agency", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    var surveyPrograms: MutableList<SurveyProgram> = mutableListOf()


    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "agency", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    var users: MutableList<User> = mutableListOf()


    override fun compareTo(other: Agency): Int {
        val i = this.id.compareTo(other.id)
        return when {
            i != 0 -> i
            else -> modified.compareTo(other.modified)
        }
    }
}
