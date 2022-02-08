package no.nsd.qddt.model.classes

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
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
abstract class AbstractEntity(
    @Id @GeneratedValue
    @Column(updatable = false, nullable = false)
    var id: UUID? = null,

    @JsonIgnore
    @Column(insertable = false, updatable = false)
    protected var modifiedById: UUID? = null,

    @Version
    var modified: Timestamp?=null
) {

    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "modifiedById")
    @Audited(targetAuditMode =  RelationTargetAuditMode.NOT_AUDITED)
    lateinit var modifiedBy: User

    abstract fun xmlBuilder(): AbstractXmlBuilder?


    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
