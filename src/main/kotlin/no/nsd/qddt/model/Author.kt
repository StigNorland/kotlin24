package no.nsd.qddt.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntity
import no.nsd.qddt.repository.handler.AuthorAuditTrailListener
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Table(name = "AUTHOR")
@EntityListeners(value = [AuthorAuditTrailListener::class])
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
data class Author(var email: String? = "") : AbstractEntity() {
    //--------------------------------------------------------------------------------
    @Column(length = 70, nullable = false)
    var name: String? = null

    @Column(length = 500)
    var about: String? = ""
    var homepageUrl: String? = ""
    var pictureUrl: String? = ""
    var authorsAffiliation: String? = ""

    @Transient
    @JsonSerialize
    @JsonDeserialize
    val classKind = "AUTHOR"

    @Transient
    @JsonSerialize
    @JsonDeserialize
    val xmlLang = "none"

//    @ManyToMany
//    var conceptReferences: MutableSet<ConceptHierarchy> = mutableSetOf()

    override fun xmlBuilder(): AbstractXmlBuilder? { return null }


}
