package no.nsd.qddt.config

import no.nsd.qddt.security.AuthTokenFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.filter.ForwardedHeaderFilter
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * @author Stig Norland
 */
@Configuration
@EnableWebMvc
//@EnableCaching
//@EnableScheduling
@EnableHypermediaSupport(type=[EnableHypermediaSupport.HypermediaType.HAL])
@EnableJpaAuditing(auditorAwareRef = "customAuditProvider")
@EnableJpaRepositories(
    basePackages = ["no.nsd.qddt.repository", "no.nsd.qddt.config", "no.nsd.qddt.security", "no.nsd.qddt.service"],
    repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean::class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    protected val logger: Logger = LoggerFactory.getLogger(SecurityConfig::class.java)

    @Value(value = "\${qddt.api.origin}")
    lateinit var origin: String


    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
    @Bean
    fun customAuditProvider(): AuditAwareImpl {
        return AuditAwareImpl()
    }

    @Bean
    fun authenticationTokenFilterBean(): AuthTokenFilter {
        return AuthTokenFilter()
    }

    @Bean
    fun passwordEncoderBean(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("http://localhost:4200")
//        config.allowedOriginPatterns =  listOf("https://*.nsd.no/","http://localhost:4200/")
        config.addAllowedHeader("*")
        config.allowedMethods = listOf("GET", "DELETE", "POST", "OPTIONS")
        logger.info(origin)
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
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


    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // Enable CORS and disable CSRF
        http.cors().and().csrf().disable()

        // Set session management to stateless
        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

        // Set unauthorized requests exception handler
        http
            .exceptionHandling()
            .authenticationEntryPoint { request: HttpServletRequest?, response: HttpServletResponse, ex: AuthenticationException ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,ex.message)
            }
            .and()

        // Set permissions on endpoints
        http.authorizeRequests() // Our public endpoints
            .antMatchers("/").permitAll()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .antMatchers("/login/**").permitAll()
            .antMatchers("/actuator/**").permitAll()
            .antMatchers(HttpMethod.GET, "/**").permitAll()
            .antMatchers(HttpMethod.GET, "/othermaterial/files/**").permitAll()
            .antMatchers(HttpMethod.DELETE, "/user/*").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/user/*").access("hasAuthority('ROLE_ADMIN') or hasPermission('OWNER')")
            .antMatchers(HttpMethod.GET, "/user/search/*").hasRole("ADMIN")
            .antMatchers(HttpMethod.PATCH, "/user/resetpassword").access("hasAuthority('ROLE_ADMIN') or hasPermission('USER')")
//            .anyRequest().authenticated()

        // Add JWT token filter
        http.addFilterBefore(authenticationTokenFilterBean(),UsernamePasswordAuthenticationFilter::class.java)

    }

//    @Throws(Exception::class)
//    override fun configure(web: WebSecurity) {
//        web.ignoring()
//            .antMatchers("/")
//            .antMatchers(HttpMethod.OPTIONS)
//
////            .antMatchers("/explorer/**")
////            .antMatchers("/actuator/**")
//    }

    // Used by spring security if CORS is enabled.


}
