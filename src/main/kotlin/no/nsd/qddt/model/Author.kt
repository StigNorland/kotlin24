package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.classes.AbstractEntity
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
// import no.nsd.qddt.domain.study.Study
// import no.nsd.qddt.domain.surveyprogram.SurveyProgram
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import java.net.URL
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Table(name = "AUTHOR")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
class Author : AbstractEntity() {
    //--------------------------------------------------------------------------------
    @Column(name = "name", length = 70, nullable = false)
    var name: String? = null
    var email: String? = null

    @Column(name = "about", length = 500)
    var about: String? = null
    var homepage: URL? = null
    var picture: URL? = null
    var authorsAffiliation: String? = null

    @Transient
    @JsonSerialize
    @JsonDeserialize
    val classKind = "AUTHOR"

    @Transient
    @JsonSerialize
    @JsonDeserialize
    val xmlLang = "none"

    // @JsonBackReference(value = "surveyRef")
    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "authors")
    // var surveyPrograms: Set<SurveyProgram> = HashSet()

    // @JsonBackReference(value = "studyRef")
    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "authors")
    // var studies: Set<Study> = HashSet()

    @JsonBackReference(value = "topicRef")
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "authors")
    var topicGroups: MutableSet<TopicGroup> = mutableSetOf()

    
    //    @PrePersist
    override val xmlBuilder: AbstractXmlBuilder?
        get() = null

}
