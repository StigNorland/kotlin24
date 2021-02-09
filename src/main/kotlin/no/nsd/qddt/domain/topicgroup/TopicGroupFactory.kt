package no.nsd.qddt.domain.topicgroup


import no.nsd.qddt.classes.IEntityFactory
import no.nsd.qddt.domain.concept.ConceptFactory
import java.util.function.Consumer
import java.util.function.Function

/**
 * @author Stig Norland
 */
internal class TopicGroupFactory : IEntityFactory<TopicGroup> {
    override fun create(): TopicGroup {
        return TopicGroup()
    }

    override fun copyBody(source: TopicGroup, dest: TopicGroup): TopicGroup {
        dest.description = source.description
        dest.name = source.name
        dest.otherMaterials = source.otherMaterials.stream()
            .map(Function<OtherMaterial, OtherMaterial> { m: OtherMaterial -> m.clone() })
            .collect(Collectors.toList())
        val cf = ConceptFactory()
        dest.setConcepts(
            source.getConcepts().stream()
                .map(Function<Concept, Any> { mapper: Concept? -> cf.copy(mapper, dest.basedOnRevision) })
                .collect(Collectors.toList())
        )
        dest.getConcepts().forEach(Consumer<Concept> { concept: Concept -> concept.setTopicGroup(dest) })
        dest.topicQuestionItems = source.topicQuestionItems.stream()
            .map(Function<ElementRefEmbedded<QuestionItem?>, ElementRefEmbedded<QuestionItem>> { obj: ElementRefEmbedded<QuestionItem?> -> obj.clone() })
            .collect(Collectors.toList())
        return dest
    }
}
