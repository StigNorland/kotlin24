package  no.nsd.qddt.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.filter.ForwardedHeaderFilter
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.config.annotation.EnableWebMvc


/**
 * @author Stig Norland
 */
@Configuration
@EnableWebMvc
//@EnableCaching
@EnableScheduling
@EnableHypermediaSupport(type=[EnableHypermediaSupport.HypermediaType.HAL])
class ApplicationConfig {

    @Bean
    fun forwardedHeaderFilter(): FilterRegistrationBean<ForwardedHeaderFilter> {
        return FilterRegistrationBean(ForwardedHeaderFilter())
    }

    @Bean
    fun cacheControlFilter(): FilterRegistrationBean<*> {
        val registration = FilterRegistrationBean<OncePerRequestFilter>(CacheControlFilter())
        registration.addUrlPatterns("/*")
        return registration
    }

//    @Scheduled(fixedRate = ONE_WEEK )
//    @CacheEvict(value = ["FYLKER", "SKOLER","KOMMUNER", "PARTIER"])
//    fun clearCache() {
//        log.info("Scheduled -> cache cleared")
//    }


}
