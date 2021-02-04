package no.nsd.qddt.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.domain.user.User
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.LastModifiedBy
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */
@Audited
@MappedSuperclass
abstract class AbstractEntity(
    @Id  @GeneratedValue
    val id: UUID? = null,

    @Version
    var  modified: Timestamp? = null,

    @LastModifiedBy
    @ManyToOne
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    var modifiedBy: User? = null

) {
    @Transient
    @JsonIgnore
    protected val LOG = LoggerFactory.getLogger(this.javaClass)

    @get:JsonIgnore
    abstract val xmlBuilder: AbstractXmlBuilder?
}
