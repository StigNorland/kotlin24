package no.nsd.qddt.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.projection.SpelAwareProxyProjectionFactory
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.web.filter.ForwardedHeaderFilter
import org.springframework.web.filter.OncePerRequestFilter


/**
 * @author Stig Norland
 */
@Configuration
@EnableCaching
@EnableJpaAuditing(auditorAwareRef = "customAuditProvider")
@EnableHypermediaSupport(type=[EnableHypermediaSupport.HypermediaType.HAL,EnableHypermediaSupport.HypermediaType.HAL_FORMS, EnableHypermediaSupport.HypermediaType.COLLECTION_JSON])
@EnableJpaRepositories(
    basePackages = ["no.nsd.qddt.repository", "no.nsd.qddt.service", "no.nsd.qddt.config", "no.nsd.qddt.security"],
    repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean::class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
class AppConfig {
    @Bean
    fun customAuditProvider(): AuditAwareImpl {
//        val test = wildify
        return AuditAwareImpl()
    }

    @Bean
    fun projectionFactory(): SpelAwareProxyProjectionFactory? {
        return SpelAwareProxyProjectionFactory()
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
