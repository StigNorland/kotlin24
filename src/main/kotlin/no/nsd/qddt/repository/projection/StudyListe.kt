package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.TopicGroup
import org.springframework.data.rest.core.config.Projection

@Projection(name = "studyListe", types = [Study::class])
interface StudyListe: IAbstractEntityEditList {
    var description: String
    // var topicGroup: List<TopicGroup>
    var isArchived: Boolean

}
