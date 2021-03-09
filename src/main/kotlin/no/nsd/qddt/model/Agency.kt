package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Where
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Cacheable
@Entity
data class Agency( var name: String="?") : Comparable<Agency> {
    @Id @GeneratedValue
    @Column(updatable = false, nullable = false)
    lateinit var id: UUID

    @Version
    lateinit var modified : Timestamp

    var xmlLang:String="en-GB"

    @JsonIgnore
    @Where(clause = "class_kind='SURVEY_PROGRAM'")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agencyId", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    var surveyPrograms: MutableSet<SurveyProgram> = mutableSetOf()

    @JsonIgnore
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
