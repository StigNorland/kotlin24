package no.nsd.qddt.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntity
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Table(name = "AUTHOR")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Cacheable
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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Author

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name ,  modified = $modified )"
    }

}
