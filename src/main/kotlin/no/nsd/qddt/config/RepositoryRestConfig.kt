package  no.nsd.qddt.config

import no.nsd.qddt.model.*
import no.nsd.qddt.repository.handler.QuestionItemEventHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry
import kotlin.sequences.Sequence

/**
 * @author Stig Norland
 */
@Component
class RepositoryRestConfig : RepositoryRestConfigurer {
    @Value(value = "\${qddt.api.origin}")
    lateinit var origin: String

    override fun configureRepositoryRestConfiguration(config:RepositoryRestConfiguration,  cors: CorsRegistry) {

        cors.addMapping("/**").allowedOrigins(origin)

        config.exposeIdsFor(Agency::class.java)
        config.exposeIdsFor(Author::class.java)
        config.exposeIdsFor(Category::class.java)
        config.exposeIdsFor(Concept::class.java)
        config.exposeIdsFor(QuestionConstruct::class.java)
        config.exposeIdsFor(QuestionItem::class.java)
        config.exposeIdsFor(ResponseDomain::class.java)
        config.exposeIdsFor(Sequence::class.java)
        config.exposeIdsFor(Study::class.java)
        config.exposeIdsFor(SurveyProgram::class.java)
        config.exposeIdsFor(TopicGroup::class.java)
        config.exposeIdsFor(Universe::class.java)
        config.exposeIdsFor(User::class.java)

        config.repositoryDetectionStrategy = RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED
    }

    @Bean
    fun questionItemEventHandler(): QuestionItemEventHandler {
        return QuestionItemEventHandler()
    }
}
