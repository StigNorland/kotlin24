package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.TopicGroup
import org.springframework.data.rest.core.config.Projection

/**
 * @author Stig Norland
 */
@Projection(name = "topicgroupListe", types = [TopicGroup::class])
interface TopicGroupListe: IAbstractEntityEditList {
    var description: String
}

