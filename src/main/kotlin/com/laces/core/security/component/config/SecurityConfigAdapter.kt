package com.laces.core.security.component.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@Configuration
@ConfigurationProperties(prefix = "laces.security")
class SecurityConfigAdapter(
        @Value("\${laces.security.includeDefaultAllowed:true}")
        val includeDefaultAllowed: Boolean,
        val authenticationProvider: DaoAuthenticationProvider,
        val loginFailureHandler: LoginFailureHandler
) : WebSecurityConfigurerAdapter() {

    companion object {
        private val LOG = LoggerFactory.getLogger(SecurityConfigAdapter::class.java)
        private const val STRIPE_WEBHOOK_URL = "/stripe/webhook"

    }

    val defaultUrls = listOf("/built/**", "/*.js", "/*.jsx", "/*.jpg", "/main.css"
            , "/auth/**", "/webjars/**", "/register-confirmation/**", "/payment/**", STRIPE_WEBHOOK_URL)

    var allowedUrls = mutableListOf<String>()
    override fun configure(http: HttpSecurity) {
        if (includeDefaultAllowed) {
            allowedUrls.addAll(defaultUrls)
            LOG.info("Including default URLs.")
        }
        LOG.info("Allowed URLS: $allowedUrls")
        http
            .authorizeRequests()
            .antMatchers(*allowedUrls.toTypedArray()).permitAll()
            .anyRequest().authenticated()
        .and()
            .formLogin()
                .permitAll()
                .successForwardUrl("/auth/success")
                .failureHandler(loginFailureHandler)
        .and()
            .logout().permitAll()
                .clearAuthentication(true)
                .addLogoutHandler(CookieClearingLogoutHandler())
            .logoutSuccessHandler((HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
        .and()
        .exceptionHandling()
            .authenticationEntryPoint(Http403ForbiddenEntryPoint())
        .and()
            .csrf()
                .ignoringAntMatchers(STRIPE_WEBHOOK_URL)
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        LOG.info("CSRF Has been updated to allow Stripe")
        http
            .sessionManagement()
            .maximumSessions(1)
            .sessionRegistry(sessionRegistry())

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

    @Bean
    fun sessionRegistry(): SessionRegistry {
        return SessionRegistryImpl()
    }
}