package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nsd.qddt.repository.handler.AgentAuditTrailListener
import org.hibernate.annotations.Where
import org.hibernate.envers.AuditMappedBy
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Cacheable
@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")

//@EntityListeners(value = [AgentAuditTrailListener::class])
data class Agency( var name: String="?") : Comparable<Agency> {

    @Id @GeneratedValue
    @Column(updatable = false, nullable = false)
    lateinit var id: UUID

    @Version
    lateinit var modified : Timestamp

    var xmlLang:String="en-GB"

    @JsonIgnore
    @JsonIgnoreProperties("agency","children")
    @OrderColumn(name = "parentIdx")
    @AuditMappedBy(mappedBy = "agency", positionMappedBy = "parentIdx")
    @OneToMany(mappedBy = "agency", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @Where(clause = "class_kind='SURVEY_PROGRAM'")
    var surveyPrograms: MutableList<SurveyProgram> = mutableListOf()

    @JsonIgnore
    @JsonIgnoreProperties("agency")
    @OneToMany(mappedBy = "agency", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    var users: MutableSet<User> = mutableSetOf()

    override fun compareTo(other: Agency): Int {
        val i = this.id.compareTo(other.id)
        return when {
            i != 0 -> i
            else -> modified.compareTo(other.modified)
        }
    }

}
