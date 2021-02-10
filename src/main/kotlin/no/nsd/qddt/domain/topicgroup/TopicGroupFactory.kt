package no.nsd.qddt.domain.topicgroup


import no.nsd.qddt.domain.concept.ConceptFactory
import no.nsd.qddt.classes.IEntityFactory
import no.nsd.qddt.classes.elementref.ElementRefEmbedded
import java.util.stream.Collectors
import java.util.UUID

/**
 * @author Stig Norland
 */
internal class TopicGroupFactory : IEntityFactory<TopicGroup> {
    override fun create(): TopicGroup {
        return TopicGroup(null,"")
    }

    override fun copyBody(source: TopicGroup, dest: TopicGroup): TopicGroup {
        return dest.apply {  
            name = source.name

            description = source.description
            // label = source.label
            otherMaterials = source.otherMaterials.stream()
            .map{ it.clone() }
            .collect(Collectors.toList())

            // concepts = source.concepts.stream()
            // .map{ copy(it, dest.basedOnRevision) })
            // .collect(Collectors.toList())
            // val cf = ConceptFactory().copy(source, revision)
            // concepts.forEach(Consumer<Concept> { concept: Concept -> concept.setTopicGroup(dest) })

            topicQuestionItems = source.topicQuestionItems.stream()
                .map{ it.clone() }
                .collect(Collectors.toList())
                
            // dest.getChildren().forEach(action -> action.setParentC(dest));
          }

    }
}
