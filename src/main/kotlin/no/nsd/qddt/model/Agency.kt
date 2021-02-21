package no.nsd.qddt.model

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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "agency", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    var surveyPrograms: MutableList<SurveyProgram> = mutableListOf()


    override fun compareTo(other: Agency): Int {
        val i = this.id.compareTo(other.id)
        return when {
            i != 0 -> i
            else -> modified.compareTo(other.modified)
        }
    }
}
