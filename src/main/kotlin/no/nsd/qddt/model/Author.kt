package no.nsd.qddt.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntity
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Transient

/**
 * @author Stig Norland
 */
@Entity
@Table(name = "AUTHOR")
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

    override fun xmlBuilder(): AbstractXmlBuilder? { return null }


}
