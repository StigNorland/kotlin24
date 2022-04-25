package  no.nsd.qddt.config

import no.nsd.qddt.model.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry


/**
 * @author Stig Norland
 */
@Component
class RepositoryRestConfig : RepositoryRestConfigurer {
    @Value(value = "\${qddt.api.origin}")
    lateinit var origin: String


    override fun configureHttpMessageConverters(messageConverters: MutableList<HttpMessageConverter<*>?>) {
        val converter = StringHttpMessageConverter()
        converter.supportedMediaTypes = configureMediaTypes()
        messageConverters.add(converter)
    }
    private fun configureMediaTypes(): List<MediaType> {
        val mediaTypes: MutableList<MediaType> = ArrayList()
        mediaTypes.add(MediaType.TEXT_PLAIN)
        mediaTypes.add(MediaType.APPLICATION_OCTET_STREAM)
        mediaTypes.add(MediaType.APPLICATION_PDF)
        mediaTypes.add(MediaType.APPLICATION_XML)
        return mediaTypes
    }

    override fun configureRepositoryRestConfiguration(config:RepositoryRestConfiguration,  cors: CorsRegistry) {

        cors.addMapping("/**").allowedOrigins(origin)

        config.exposeIdsFor(User::class.java)
        config.exposeIdsFor(Agency::class.java)
        config.exposeIdsFor(Author::class.java)
        config.exposeIdsFor(Authority::class.java)
        config.exposeIdsFor(Category::class.java)
        config.exposeIdsFor(Comment::class.java)
        config.exposeIdsFor(Instruction::class.java)
        config.exposeIdsFor(Instrument::class.java)
        config.exposeIdsFor(InstrumentElement::class.java)
        config.exposeIdsFor(Sequence::class.java)
        config.exposeIdsFor(StatementItem::class.java)
        config.exposeIdsFor(ConditionConstruct::class.java)
        config.exposeIdsFor(QuestionConstruct::class.java)
        config.exposeIdsFor(QuestionItem::class.java)
        config.exposeIdsFor(ResponseDomain::class.java)
        config.exposeIdsFor(SurveyProgram::class.java)
        config.exposeIdsFor(Study::class.java)
        config.exposeIdsFor(TopicGroup::class.java)
        config.exposeIdsFor(Concept::class.java)
        config.exposeIdsFor(Universe::class.java)
        config.exposeIdsFor(PublicationStatus::class.java)
        config.exposeIdsFor(Publication::class.java)

//        config.withEntityLookup().forRepository(AgencyRepository::class.java, Agency::id, AgencyRepository::findById)

        config.repositoryDetectionStrategy = RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED
    }


}
