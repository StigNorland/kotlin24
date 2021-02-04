package no.nsd.qddt.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * @author Stig Norland
 */
@Configuration //@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
//@EnableJpaAuditing(auditorAwareRef = "customAuditProvider")
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean::class)
class AuditAwareJpaConfig {
    @Bean
    fun customAuditProvider(): AuditAwareImpl {
        return AuditAwareImpl()
    }
}
