package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.Hibernate
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
    @OrderColumn(name = "parentIdx")
    @AuditMappedBy(mappedBy = "agency", positionMappedBy = "parentIdx")
    @OneToMany(mappedBy = "agency", cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.LAZY)
    @Where(clause = "class_kind='SURVEY_PROGRAM'")
    var surveyPrograms: MutableList<SurveyProgram> = mutableListOf()

    @JsonIgnore
    @OneToMany(mappedBy = "agency", cascade = [CascadeType.REMOVE, CascadeType.PERSIST], fetch = FetchType.LAZY)
    var users: MutableSet<User> = mutableSetOf()

    override fun compareTo(other: Agency): Int {
        val i = this.id.compareTo(other.id)
        return when {
            i != 0 -> i
            else -> modified.compareTo(other.modified)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Agency

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , modified = $modified )"
    }

}
