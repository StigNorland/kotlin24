package no.nsd.qddt.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.web.filter.ForwardedHeaderFilter
import org.springframework.web.filter.OncePerRequestFilter


/**
 * @author Stig Norland
 */
@Configuration
@EnableCaching
@EnableJpaAuditing(auditorAwareRef = "customAuditProvider")
@EnableHypermediaSupport(type=[EnableHypermediaSupport.HypermediaType.HAL])
@EnableJpaRepositories(
    basePackages = ["no.nsd.qddt.repository", "no.nsd.qddt.config", "no.nsd.qddt.security", "no.nsd.qddt.service"],
    repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean::class)
class AppConfig {
    @Bean
    fun customAuditProvider(): AuditAwareImpl {
        return AuditAwareImpl()
    }

    @Bean
    fun forwardedHeaderFilter(): FilterRegistrationBean<ForwardedHeaderFilter> {
        return FilterRegistrationBean(ForwardedHeaderFilter())
    }

    @Bean
    fun cacheControlFilter(): FilterRegistrationBean<OncePerRequestFilter> {
        val registration = FilterRegistrationBean<OncePerRequestFilter>(CacheControlFilter())
        registration.addUrlPatterns("/*")
        return registration
    }

}
