package no.nsd.qddt.config

import no.nsd.qddt.model.interfaces.IBasedOn
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
//        val test = wildify
        return AuditAwareImpl()
    }

    @Bean
    fun projectionFactory(): SpelAwareProxyProjectionFactory? {
        return SpelAwareProxyProjectionFactory()
    }

//    @Bean
//    fun userReasonBean(): IBasedOn {
//        return {  } as IBasedOn
//    }

//    @Autowired
//    private val env: Environment? = null
//
//    @Bean
//    fun dataSource(): DataSource {
//       return DriverManagerDataSource().apply {
//            env?.getProperty("datasource.driverClassName")?.let { setDriverClassName(it) }
//            env?.getProperty("datasource.url")?.let { url = it }
//            env?.getProperty("datasource.username")?.let { username = it }
//            env?.getProperty("datasource.password")?.let { password = it }
//
//        }
//    }
//
//    @Bean
//    fun entityManagerFactory(): EntityManagerFactory? {
//        return LocalContainerEntityManagerFactoryBean().apply {
//            setPackagesToScan("no.nsd.qddt.repository")
//            jpaVendorAdapter = HibernateJpaVendorAdapter().apply {
//                setGenerateDdl(true)
//            }
//            dataSource = dataSource()
//            afterPropertiesSet()
//
//        }.`object`
//    }

//    @Bean
//    fun transactionManager(): PlatformTransactionManager? {
//        return JpaTransactionManager().apply {
//            entityManagerFactory = entityManagerFactory()
//        }
//    }



    @Bean
    fun forwardedHeaderFilter(): FilterRegistrationBean<ForwardedHeaderFilter> {
        return FilterRegistrationBean(ForwardedHeaderFilter())
    }

//    @Bean
//    fun cacheControlFilter(): FilterRegistrationBean<OncePerRequestFilter> {
//        val registration = FilterRegistrationBean<OncePerRequestFilter>(CacheControlFilter())
//        registration.addUrlPatterns("/*")
//        return registration
//    }

}
