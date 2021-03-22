package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.repository.handler.AgentAuditTrailListener
import org.hibernate.annotations.Where
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Cacheable
@Entity
@EntityListeners(value = [AgentAuditTrailListener::class])
data class Agency( var name: String="?") : Comparable<Agency> {

    @Id @GeneratedValue
    @Column(updatable = false, nullable = false)
    lateinit var id: UUID

    @Version
    lateinit var modified : Timestamp

    var xmlLang:String="en-GB"

    @JsonIgnore
    @Where(clause = "class_kind='SURVEY_PROGRAM'")
    @OneToMany(mappedBy = "agencyId", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    var surveyPrograms: MutableSet<SurveyProgram> = mutableSetOf()

    @JsonIgnore
    @OneToMany(mappedBy = "agencyId", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    var users: MutableSet<User> = mutableSetOf()

    override fun compareTo(other: Agency): Int {
        val i = this.id.compareTo(other.id)
        return when {
            i != 0 -> i
            else -> modified.compareTo(other.modified)
        }
    }

}
