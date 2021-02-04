package  no.nsd.qddt.config

import no.nsd.qddt.domain.agency.Agency
import no.nsd.qddt.domain.user.User
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry

/**
 * @author Stig Norland
 */
@Component
class RepositoryRestConfig : RepositoryRestConfigurer {
    override fun configureRepositoryRestConfiguration(config:RepositoryRestConfiguration,  cors: CorsRegistry) {

        cors.addMapping("/**").allowedOrigins("*")

        config.exposeIdsFor(User::class.java)
        config.exposeIdsFor(Agency::class.java)

        config.repositoryDetectionStrategy = RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED
    }
}
