package no.nsd.qddt.domain.topicgroup

import no.nsd.qddt.classes.interfaces.IWebMenuPreview
import no.nsd.qddt.domain.user.User
import no.nsd.qddt.domain.agency.Agency
import org.springframework.data.rest.core.config.Projection
import java.sql.Timestamp
import java.util.*

/**
 * @author Stig Norland
 */
@Projection(name = "topicgroupListe", types = [TopicGroup::class])
interface TopicGroupListe:IWebMenuPreview {
    var modified: Timestamp
    var modifiedBy: User
    var agency: Agency
    var classKind: String
}

