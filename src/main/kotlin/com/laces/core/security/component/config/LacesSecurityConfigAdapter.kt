package com.laces.core.security.component.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.annotation.PostConstruct


@Order(0)
@Configuration
@ConfigurationProperties(prefix = "laces.security")
class LacesSecurityConfigAdapter(
        @Value("\${laces.security.includeDefaultAllowed:true}")
        val includeDefaultAllowed: Boolean,
        val authenticationProvider: DaoAuthenticationProvider,
        val loginFailureHandler: LoginFailureHandler
) : WebSecurityConfigurerAdapter() {

    companion object {
        private val LOG = LoggerFactory.getLogger(LacesSecurityConfigAdapter::class.java)
        const val STRIPE_WEBHOOK_URL = "/stripe/webhook"
    }

    final val defaultUrls = listOf(
            "/*.js",
            "/*.jpg",
            "/*.css",
            "/auth/**",
            "/register-confirmation/**",
            "/payment/**",
            "/api/form/**",
            "/api/contact/**",
            "/api/password/reset/**",
            STRIPE_WEBHOOK_URL
    )

    var allowedUrls = mutableListOf<String>()
    var authenticatedUrls = mutableListOf<String>()

    @PostConstruct
    fun postConstruct() {
        if (includeDefaultAllowed) {
            allowedUrls.addAll(defaultUrls)
            LOG.info("Including default URLs.")
        }
    }

    override fun configure(http: HttpSecurity) {

        LOG.info("Allowed URLS: $allowedUrls")
        LOG.info("Authenticated URLS: $authenticatedUrls")

        http
                .authorizeRequests()
                .antMatchers(*allowedUrls.toTypedArray()).permitAll()
                .antMatchers(*authenticatedUrls.toTypedArray()).authenticated()
            .and()
                .formLogin()
                .permitAll()
                .successForwardUrl("/auth/success")
                .failureHandler(loginFailureHandler)
            .and()
                .logout().permitAll()
                .clearAuthentication(true)
                .logoutSuccessHandler((HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(Http403ForbiddenEntryPoint())
            .and()
                .csrf()
                .ignoringAntMatchers(*listOf(listOf(STRIPE_WEBHOOK_URL), allowedUrls).flatten().toTypedArray())
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
                .requiresChannel()
                .requestMatchers(RequestMatcher{it.getHeader("X-Forwarded-Proto") != null})
                .requiresSecure()
            .and()
                .cors()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        allowedUrls.forEach { source.registerCorsConfiguration(it, configuration) }
        return source
    }


    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(authenticationProvider)
    }

    /**
     * As of Spring Boot 2, this bean needs to be explicitly exposed.
     */
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}