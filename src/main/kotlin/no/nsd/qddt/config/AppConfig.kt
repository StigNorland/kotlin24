package no.nsd.qddt.config

import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.interfaces.IBasedOn
import org.hibernate.cfg.AvailableSettings
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.projection.SpelAwareProxyProjectionFactory
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.orm.hibernate5.SpringBeanContainer
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.web.filter.ForwardedHeaderFilter
import java.util.Map
import javax.sql.DataSource


/**
 * @author Stig Norland
 */
@Configuration
@EnableCaching
@EnableJpaAuditing(auditorAwareRef = "customAuditProvider")
@EnableHypermediaSupport(type=[EnableHypermediaSupport.HypermediaType.HAL,EnableHypermediaSupport.HypermediaType.HAL_FORMS, EnableHypermediaSupport.HypermediaType.COLLECTION_JSON])
@EnableJpaRepositories(
    basePackages = ["no.nsd.qddt.repository", "no.nsd.qddt.service", "no.nsd.qddt.config", "no.nsd.qddt.security","no.nsd.qddt.repository.handler"],
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
//
//    @Bean(name = ["entityManagerFactory"])
//    fun entityManagerFactory(
//        dataSource: DataSource?,
//        builder: EntityManagerFactoryBuilder,
//        beanFactory: ConfigurableListableBeanFactory?): LocalContainerEntityManagerFactoryBean
//    {
//        return builder.dataSource(dataSource)
//            .packages(AbstractEntityAudit::class.java)
//            .persistenceUnit("myunit")
//            .properties(Map.of(AvailableSettings.BEAN_CONTAINER, SpringBeanContainer(beanFactory!!)))
//            .build()
//    }

//    @Bean
//    fun cacheControlFilter(): FilterRegistrationBean<OncePerRequestFilter> {
//        val registration = FilterRegistrationBean<OncePerRequestFilter>(CacheControlFilter())
//        registration.addUrlPatterns("/*")
//        return registration
//    }

}
