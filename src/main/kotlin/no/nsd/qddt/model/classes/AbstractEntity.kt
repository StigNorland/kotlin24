package no.nsd.qddt.model.classes

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.model.User
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode
import org.slf4j.Logger
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
abstract class AbstractEntity {
    @Transient
    @JsonIgnore
    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Id @GeneratedValue
    @Column(updatable = false, nullable = false)
    lateinit var id: UUID

    @ManyToOne
    @LastModifiedBy
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    lateinit var modifiedBy: User

    @Version
    lateinit var modified: Timestamp


    abstract val xmlBuilder: AbstractXmlBuilder?
}
