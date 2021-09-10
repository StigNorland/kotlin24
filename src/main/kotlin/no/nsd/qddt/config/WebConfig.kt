package no.nsd.qddt.config
//
///**
// * @author Stig Norland
// */
//import no.nsd.qddt.security.AuthUserDetailsService
//import no.nsd.qddt.security.JWTAuthenticationFilter
//import no.nsd.qddt.security.JWTAuthorizationFilter
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.boot.context.properties.EnableConfigurationProperties
//import org.springframework.boot.web.servlet.FilterRegistrationBean
//import org.springframework.cache.annotation.EnableCaching
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories
//import org.springframework.hateoas.config.EnableHypermediaSupport
//import org.springframework.http.HttpMethod
//import org.springframework.security.authentication.AuthenticationManager
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
//import org.springframework.security.config.http.SessionCreationPolicy
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.web.cors.CorsConfiguration
//import org.springframework.web.cors.CorsConfigurationSource
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource
//import org.springframework.web.filter.OncePerRequestFilter
//
//
//@Configuration
//@EnableJpaRepositories(
//    basePackages = ["no.nsd.qddt.repository", "no.nsd.qddt.config", "no.nsd.qddt.security", "no.nsd.qddt.service"],
//    repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean::class)
//@EnableCaching
//@EnableHypermediaSupport(type=[EnableHypermediaSupport.HypermediaType.HAL])
//@EnableJpaAuditing(auditorAwareRef = "customAuditProvider")
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableConfigurationProperties
//class WebConfig(
//    val userDetailsService: AuthUserDetailsService,
//    val securityProperties: SecurityProperties
//) : WebSecurityConfigurerAdapter() {
//    protected val logger: Logger = LoggerFactory.getLogger(WebConfig::class.java)
//
//    override fun configure(http: HttpSecurity) {
//        http
//            .cors().and()
//            .csrf().disable()
//            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no sessions
//            .and()
//            .authorizeRequests()
//            .antMatchers("/api/**").permitAll()
//            .antMatchers("/error/**").permitAll()
//            .antMatchers(HttpMethod.POST, "/login").permitAll()
//            .anyRequest().authenticated()
//            .and()
//            .addFilter(JWTAuthenticationFilter(authenticationManager(), securityProperties))
//            .addFilter(JWTAuthorizationFilter(authenticationManager(), securityProperties))
//    }
//
//
//    @Value(value = "\${qddt.api.origin}")
//    lateinit var origin: String
//
////    @Bean
////    fun corsFilter(): CorsFilter {
////        val source = UrlBasedCorsConfigurationSource()
////        val config = CorsConfiguration()
////        config.allowCredentials = true
////        config.addAllowedOrigin("http://localhost:4200")
//////        config.allowedOriginPatterns =  listOf("https://*.nsd.no/","http://localhost:4200/")
////        config.addAllowedHeader("*")
////        config.allowedMethods = listOf("*") // listOf("GET", "DELETE", "POST", "OPTIONS")
////        logger.info(origin)
////        source.registerCorsConfiguration("/**", config)
////        return CorsFilter(source)
////    }
////
////    @Bean
////    fun forwardedHeaderFilter(): FilterRegistrationBean<ForwardedHeaderFilter> {
////        return FilterRegistrationBean(ForwardedHeaderFilter())
////    }
////
//
//    @Bean
//    fun cacheControlFilter(): FilterRegistrationBean<OncePerRequestFilter> {
//        val registration = FilterRegistrationBean<OncePerRequestFilter>(CacheControlFilter())
//        registration.addUrlPatterns("/*")
//        return registration
//    }
//
//
////    @Bean
////    @Throws(Exception::class)
////    override fun authenticationManagerBean(): AuthenticationManager {
////        return super.authenticationManagerBean()
////    }
//    @Bean
//    fun customAuditProvider(): AuditAwareImpl {
//        return AuditAwareImpl()
//    }
//        @Bean
//    fun passwordEncoderBean(): PasswordEncoder {
//        return BCryptPasswordEncoder()
//    }
//    @Throws(Exception::class)
//    override fun configure(auth: AuthenticationManagerBuilder) {
//        auth.userDetailsService(userDetailsService)
//            .passwordEncoder(passwordEncoderBean())
//    }
//
//    @Bean
//    fun authProvider(): DaoAuthenticationProvider = DaoAuthenticationProvider().apply {
//        setUserDetailsService(userDetailsService)
//        setPasswordEncoder(passwordEncoderBean())
//    }
//
//    @Bean
//    fun corsConfigurationSource(): CorsConfigurationSource = UrlBasedCorsConfigurationSource().also { cors ->
//        CorsConfiguration().apply {
//            allowedOrigins = listOf("*")
//            allowedMethods = listOf("POST", "PUT", "DELETE", "GET", "OPTIONS", "HEAD")
//            allowedHeaders = listOf(
//                "Authorization",
//                "Content-Type",
//                "X-Requested-With",
//                "Accept",
//                "Origin",
//                "Access-Control-Request-Method",
//                "Access-Control-Request-Headers"
//            )
//            exposedHeaders = listOf(
//                "Access-Control-Allow-Origin",
//                "Access-Control-Allow-Credentials",
//                "Authorization",
//                "Content-Disposition"
//            )
//            maxAge = 3600
//            cors.registerCorsConfiguration("/**", this)
//        }
//    }
//}
