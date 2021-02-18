package no.nsd.qddt.config

import no.nsd.qddt.security.AuthTokenFilter
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
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
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse





/**
 * @author Stig Norland
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
class SecurityConfig1 : WebSecurityConfigurerAdapter() {

    @Bean
    fun authenticationTokenFilterBean(): AuthTokenFilter {
        return AuthTokenFilter()
    }

    @Bean
    fun passwordEncoderBean(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(java.lang.Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
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
            .antMatchers("/auth/**").permitAll()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers(HttpMethod.GET, "/othermaterial/files/**").permitAll()
            .antMatchers(HttpMethod.GET, "/**").permitAll()
            .antMatchers(HttpMethod.DELETE, "/user/*").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/user/*").access("hasAuthority('ROLE_ADMIN') or hasPermission('OWNER')")
            .antMatchers(HttpMethod.GET, "/user/page/search/*").hasRole("ADMIN")
            .antMatchers(HttpMethod.PATCH, "/user/resetpassword").access("hasAuthority('ROLE_ADMIN') or hasPermission('USER')")
//            .anyRequest().authenticated()

        // Add JWT token filter
        http.addFilterBefore(authenticationTokenFilterBean(),UsernamePasswordAuthenticationFilter::class.java)

    }

    @Throws(java.lang.Exception::class)
    override fun configure(web: WebSecurity) {
        web.ignoring()
            .antMatchers("/explorer/**")
            .antMatchers("/actuator/**")
    }

    // Used by spring security if CORS is enabled.
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

}
