package no.nsd.qddt.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.config.annotation.CorsRegistry

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer





/**
 * @author Stig Norland
 */
@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {
    @Value("\${api.origin}")
    lateinit var origin: String

    @Autowired
    private lateinit var jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint

    @Autowired
    private lateinit var jwtUserDetailsService: UserDetailsService

    @Autowired
    private lateinit var jwtRequestFilter: JwtRequestFilter


    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
            .antMatchers("/health").permitAll()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers(HttpMethod.GET, "/preview/**").permitAll()
            .antMatchers(HttpMethod.GET, "/othermaterial/files/**").permitAll()
            .antMatchers("/auth/signin").permitAll()
            .antMatchers(HttpMethod.DELETE, "/user/*").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/user/*").access("hasAuthority('ROLE_ADMIN') or hasPermission('OWNER')")
            .antMatchers(HttpMethod.GET, "/user/page/search/*").hasRole("ADMIN")
            .antMatchers(HttpMethod.PATCH, "/user/resetpassword")
            .access("hasAuthority('ROLE_ADMIN') or hasPermission('USER')")
            .anyRequest().authenticated().and()
            .csrf().disable()
            .cors()
        http
            .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter::class.java)
        http
            .headers().cacheControl()
    }


    @Bean
    fun authenticationTokenFilterBean(): JwtRequestFilter {
        return JwtRequestFilter()
    }


    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoderBean())
    }

    @Bean
    fun passwordEncoderBean(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }

//    @Bean
//    fun corsConfigurationSource(): CorsConfigurationSource? {
//        val source = UrlBasedCorsConfigurationSource()
//        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
//        return source
//    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer? {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(origin.split(",").toString())
            }
        }
    }

//    @Throws(Exception::class)
//    override fun configure(httpSecurity: HttpSecurity) {
//
//        httpSecurity.csrf().disable() // dont authenticate this particular request
//            .authorizeRequests().antMatchers("/auth/signin").permitAll()
//            .antMatchers(HttpMethod.OPTIONS, "/**")
//            .permitAll().anyRequest() // all other requests need to be authenticated
//            .authenticated().and().exceptionHandling() // make sure we use stateless session; session won't be used to
//            .authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//
//        // Add a filter to validate the tokens with every request
//        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
//    }
}
