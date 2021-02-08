package no.nsd.qddt.domain.author

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.domain.AbstractEntity
import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.domain.study.Study
import no.nsd.qddt.domain.surveyprogram.SurveyProgram
import no.nsd.qddt.domain.topicgroup.TopicGroup
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import java.net.URL
import java.util.*
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

    @JsonBackReference(value = "surveyRef")
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "authors")
    var surveyPrograms: Set<SurveyProgram> = HashSet()

    @JsonBackReference(value = "studyRef")
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "authors")
    var studies: Set<Study> = HashSet()

    @JsonBackReference(value = "topicRef")
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "authors")
    var topicGroups: Set<TopicGroup> = HashSet()
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Author) return false
        if (!super.equals(o)) return false
        val author = o
        if (if (name != null) name != author.name else author.name != null) return false
        if (if (email != null) email != author.email else author.email != null) return false
        if (if (about != null) about != author.about else author.about != null) return false
        if (if (homepage != null) homepage != author.homepage else author.homepage != null) return false
        return if (picture != null) picture == author.picture else author.picture == null
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + if (email != null) email.hashCode() else 0
        result = 31 * result + if (about != null) about.hashCode() else 0
        result = 31 * result + if (homepage != null) homepage.hashCode() else 0
        result = 31 * result + if (picture != null) picture.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "{\"_class\":\"Author\", " +
                "\"name\":" + (if (name == null) "null" else "\"" + name + "\"") + ", " +
                "\"email\":" + (if (email == null) "null" else "\"" + email + "\"") + ", " +
                "\"about\":" + (if (about == null) "null" else "\"" + about + "\"") + ", " +
                "\"homepage\":" + (if (homepage == null) "null" else homepage) + ", " +
                "\"picture\":" + (if (picture == null) "null" else picture) + ", " +
                "\"authorsAffiliation\":" + (if (authorsAffiliation == null) "null" else "\"" + authorsAffiliation + "\"") +
                "}"
    }

    //    @PrePersist
    override val xmlBuilder: AbstractXmlBuilder?
        get() = null
    //    private void onInsert(){
    //        // LOG.info("PrePersist " + this.getClass().getSimpleName());
    //        User user = SecurityContext.getUserDetails().getUser();
    //        setModifiedBy( user );
    //    }
    //    @PreUpdate
    //    private void onUpdate() {
    //        try {
    //            // LOG.info( "PreUpdate " + this.getClass().getSimpleName() + " - " + name );
    //            User user = SecurityContext.getUserDetails().getUser();
    //            setModifiedBy( user );
    //        } catch (Exception ex) {
    //            //
    //        }
    //    }
}
