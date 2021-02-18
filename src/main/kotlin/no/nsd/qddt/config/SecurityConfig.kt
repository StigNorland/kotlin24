//package no.nsd.qddt.config
//
//import no.nsd.qddt.security.AuthEntryPointJwt
//import no.nsd.qddt.security.AuthTokenFilter
//import no.nsd.qddt.security.AuthUserDetailsService
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.http.HttpMethod
//import org.springframework.security.authentication.AuthenticationManager
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.builders.WebSecurity
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
//import org.springframework.security.config.http.SessionCreationPolicy
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
//
//
///**
// * @author Stig Norland
// */
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(
////    securedEnabled = true,
////    jsr250Enabled = true,
//    prePostEnabled = true)
//class SecurityConfig : WebSecurityConfigurerAdapter() {
//
//    @Value("\${qddt.api.origin}")
//    lateinit var origin: String
//
//    @Autowired
//    lateinit var userDetailsService: AuthUserDetailsService
//
//    @Autowired
//    private lateinit var unauthorizedHandler: AuthEntryPointJwt
//
//
//    @Bean
//    fun authenticationTokenFilterBean(): AuthTokenFilter {
//        return AuthTokenFilter()
//    }
//
//    @Bean
//    @Throws(Exception::class)
//    override fun authenticationManagerBean(): AuthenticationManager {
//        return super.authenticationManagerBean()
//    }
//
//    @Bean
//    fun passwordEncoderBean(): PasswordEncoder {
//        return BCryptPasswordEncoder()
//    }
//
//
//
//    @Throws(Exception::class)
//    override fun configure(authBuilder: AuthenticationManagerBuilder) {
//        authBuilder
//            .userDetailsService(userDetailsService)
//            .passwordEncoder(passwordEncoderBean())
//    }
//
//
//    @Throws(Exception::class)
//    override fun configure(httpSecurity: HttpSecurity) {
//
//
//        httpSecurity.csrf().disable() // dont authenticate this particular request
//            .authorizeRequests().antMatchers("/auth/signin").permitAll()
//            .antMatchers(HttpMethod.OPTIONS, "/**")
//            .permitAll().anyRequest() // all other requests need to be authenticated
//            .authenticated().and().exceptionHandling() // make sure we use stateless session; session won't be used to
//            .authenticationEntryPoint(unauthorizedHandler).and()
//            .exceptionHandling().accessDeniedPage("/auth/signin").and()
//            .sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//
//        // Add a filter to validate the tokens with every request
////        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
//        httpSecurity
//            .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter::class.java)
//            .headers().cacheControl()
//
////        http.cors().and().csrf().disable()
////            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
////            .exceptionHandling().accessDeniedPage("/auth/signin").and()
////            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
////            .authorizeRequests()
////                .antMatchers("/auth/**").permitAll()
////                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
////                .antMatchers(HttpMethod.GET, "/othermaterial/files/**").permitAll()
////                .antMatchers(HttpMethod.GET, "/**").hasAnyAuthority()
////                .antMatchers(HttpMethod.DELETE, "/user/*").hasRole("ADMIN")
////                .antMatchers(HttpMethod.POST, "/user/*").access("hasAuthority('ROLE_ADMIN') or hasPermission('OWNER')")
////                .antMatchers(HttpMethod.GET, "/user/page/search/*").hasRole("ADMIN")
////                .antMatchers(HttpMethod.PATCH, "/user/resetpassword").access("hasAuthority('ROLE_ADMIN') or hasPermission('USER')")
////                .anyRequest().authenticated()
////
////        http.apply( JwtTokenFilterConfigurer(jwtTokenProvider))
////
////        http
////            .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter::class.java)
////            .headers().cacheControl()
//    }
//
//
//    @Throws(java.lang.Exception::class)
//    override fun configure(web: WebSecurity) {
//        web.ignoring()
//            .antMatchers("/explorer/**")
//            .antMatchers("/actuator/**")
////            .and().ignoring()
////            .antMatchers("/h2-console/**/**")
//    }
//
////    @Autowired
////    @Throws(Exception::class)
////    fun configureGlobal(auth: AuthenticationManagerBuilder) {
////        // configure AuthenticationManager so that it knows from where to load
////        // user for matching credentials
////        // Use BCryptPasswordEncoder
////        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoderBean())
////    }
//
//
////    @Bean
////    fun corsConfigurationSource(): CorsConfigurationSource? {
////        val source = UrlBasedCorsConfigurationSource()
////        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
////        return source
////    }
//
////    @Bean
////    fun corsConfigurer(): WebMvcConfigurer? {
////        return object : WebMvcConfigurer {
////            override fun addCorsMappings(registry: CorsRegistry) {
////                registry.addMapping("/api/**")
////                    .allowedOrigins(origin.split(",").toString())
////            }
////        }
////    }
//
////    @Throws(Exception::class)
////    override fun configure(httpSecurity: HttpSecurity) {
////
////        httpSecurity.csrf().disable() // dont authenticate this particular request
////            .authorizeRequests().antMatchers("/auth/signin").permitAll()
////            .antMatchers(HttpMethod.OPTIONS, "/**")
////            .permitAll().anyRequest() // all other requests need to be authenticated
////            .authenticated().and().exceptionHandling() // make sure we use stateless session; session won't be used to
////            .authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
////            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////
////        // Add a filter to validate the tokens with every request
////        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
////    }
//}
