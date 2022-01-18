package no.nsd.qddt.config


import no.nsd.qddt.security.AuthTokenFilter
import no.nsd.qddt.security.AuthUserDetailsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.spel.spi.EvaluationContextExtension
import org.springframework.http.HttpMethod
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds.AUTHENTICATION_MANAGER
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * @author Stig Norland
 */
@Configuration
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

    @Autowired
    private lateinit var authUserDetailsService: AuthUserDetailsService

//    @Autowired
//    private  lateinit var permission: PermissionEvaluatorImpl

    @Bean
    fun authenticationTokenFilterBean(): AuthTokenFilter {
        return AuthTokenFilter()
    }

    @Bean
    fun bCryptPasswordEncoderBean(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

//    @Bean
//    fun securityExtension(): EvaluationContextExtension? {
//        return SecurityEvaluationContextExtension()
//    }
//
//    @Bean
//    fun securityExtension(): EvaluationContextExtension? {
//        return ExtensionAwareQueryMethodEvaluationContextProvider.DEFAULT
//    }
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        logger.info(origin)
        config.allowCredentials = true
        config.allowedOriginPatterns =  listOf("https://*.nsd.no","http://localhost","http://localhost:4200" )
        config.addAllowedHeader("*")
        config.allowedMethods = listOf("*") // listOf("GET", "DELETE", "POST", "OPTIONS")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    @Bean(AUTHENTICATION_MANAGER)
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun permission() : PermissionEvaluator {
        return PermissionEvaluatorImpl()
    }

    @Throws(java.lang.Exception::class)
    override fun configure(web: WebSecurity) {
        val handler = DefaultWebSecurityExpressionHandler()
        handler.setPermissionEvaluator(permission())
        web.expressionHandler(handler)
    }


    @Throws(Exception::class)
    override fun configure( auth: AuthenticationManagerBuilder){
        auth
            .userDetailsService(authUserDetailsService)
            .passwordEncoder(bCryptPasswordEncoderBean())
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
            .authenticationEntryPoint { _: HttpServletRequest?, response: HttpServletResponse, ex: AuthenticationException ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,ex.message)
            }
            .and()

        // Set permissions on endpoints
        http.authorizeRequests()
            .antMatchers("/login/**").permitAll()
//            .antMatchers(HttpMethod.GET, "/surveyprogram/*").access("@permission.hasPermission(#instance,'AGENCY')")
            .antMatchers(HttpMethod.POST, "/**").access("hasRole('ADMIN')") // or @permission.hasPermission(domainObject,'OWNER')")
            .antMatchers(HttpMethod.PUT, "/**").access("hasRole('ADMIN')") // or @permission.hasPermission(domainObject,'OWNER')")
            .antMatchers(HttpMethod.PATCH, "/**").access("hasRole('ADMIN')") // or @permission.hasPermission(domainObject,'OWNER')")
            .antMatchers(HttpMethod.POST, "/**").access("hasRole('ADMIN')") // or @permission.hasPermission(domainObject,'OWNER')")
//            .antMatchers(HttpMethod.POST, "/user/*").access("hasRole('ADMIN') or @permission.hasPermission('OWNER')")
            .antMatchers(HttpMethod.GET, "/user/search/*").hasRole("ADMIN")
            .antMatchers(HttpMethod.PATCH, "/user/resetpassword").access("hasRole('ADMIN')") // or @permission.hasPermission(domainObject,'USER')")
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .antMatchers(HttpMethod.GET).permitAll()
            .antMatchers("/actuator/**").permitAll()
            .antMatchers("/explorer/**").permitAll()
            .antMatchers("/health/**").permitAll()
            .antMatchers("/browser/**").permitAll()
            .anyRequest().authenticated()

        // Add JWT token filter
        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter::class.java)

    }

}
