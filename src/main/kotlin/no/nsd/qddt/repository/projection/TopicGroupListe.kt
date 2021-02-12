package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.interfaces.IWebMenuPreview
import no.nsd.qddt.model.User
import no.nsd.qddt.model.Agency
import no.nsd.qddt.model.TopicGroup
import org.springframework.data.rest.core.config.Projection
import java.sql.Timestamp

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

