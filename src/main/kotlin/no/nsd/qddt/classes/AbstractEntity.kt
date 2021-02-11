package no.nsd.qddt.classes

import no.nsd.qddt.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.domain.user.User
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.LastModifiedBy
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import org.slf4j.LoggerFactory
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */
@Audited
@MappedSuperclass
abstract class AbstractEntity {
    @Transient
    @JsonIgnore
    protected val logger = LoggerFactory.getLogger(this.javaClass)

    @Id  @GeneratedValue
    lateinit var id: UUID

    @ManyToOne
    @LastModifiedBy
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    lateinit var modifiedBy: User

    @Version
    lateinit var modified: Timestamp


    abstract val xmlBuilder: AbstractXmlBuilder?
}
