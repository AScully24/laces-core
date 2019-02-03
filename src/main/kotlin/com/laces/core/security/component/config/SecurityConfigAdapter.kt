package com.laces.core.security.component.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import java.io.IOException
import javax.servlet.http.HttpServletResponse
import org.springframework.security.web.RedirectStrategy
import java.util.HashMap
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import javax.servlet.http.HttpServletRequest


@Configuration
@ConfigurationProperties(prefix = "laces.security")
class SecurityConfigAdapter(
        val authenticationProvider: DaoAuthenticationProvider,
        val loginFailureHandler: LoginFailureHandler
) : WebSecurityConfigurerAdapter() {

    companion object {
        private val LOG = LoggerFactory.getLogger(SecurityConfigAdapter::class.java)
    }

    val defaultUrls = listOf("/built/**", "/*.js", "/*.jsx", "/*.jpg", "/main.css"
            , "/auth/**", "/h2-console/**", "/swagger.html", "/swagger-ui.html", "/swagger-resources/**",
            "/v2/**", "/webjars/**", "/register-confirmation/**", "/payment/**","/stripe/webhook")

    var allowedUrls = mutableListOf<String>()
    var includeDefaults = true

    override fun configure(http: HttpSecurity) {
        if (includeDefaults) {
            allowedUrls.addAll(defaultUrls)
            LOG.info("Including default URLs.")
        }
        LOG.info("Allowed URLS: $allowedUrls")
        http
            .authorizeRequests()
            .antMatchers(*allowedUrls.toTypedArray()).permitAll()
            .anyRequest().authenticated()
        .and()
            .formLogin().permitAll()
                .successForwardUrl("/auth/success")
//                .failureForwardUrl("/auth/failure")
                .failureHandler(loginFailureHandler)
        .and()
            .logout().permitAll()
            .logoutSuccessHandler((HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
        .and()
                // Needed to access the h2-console. This should be remove in final deployment
            .headers()
            .frameOptions()
            .disable()
        .and()
            .csrf().disable()

        http
            .sessionManagement()
            .maximumSessions(1)
            .sessionRegistry(sessionRegistry())

    }
//
//    @Bean
//    fun customAuthenticationFailureHandler(): AuthenticationFailureHandler {
//
//        val exceptionMappingAuthenticationFailureHandler = ExceptionMappingAuthenticationFailureHandler()
//        val map = HashMap<Any, Any>()
//        map["org.springframework.security.authentication.CredentialsExpiredException"] = "/resetPassword.html"
//        exceptionMappingAuthenticationFailureHandler.setExceptionMappings(map)
//        exceptionMappingAuthenticationFailureHandler
//                .setRedirectStrategy { request, response, url -> response.sendRedirect(request.contextPath + url) }
//
//        return exceptionMappingAuthenticationFailureHandler
//    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(authenticationProvider)
    }

    @Bean
    fun sessionRegistry(): SessionRegistry {
        return SessionRegistryImpl()
    }

    /**
     * As of Spring Boot 2, this bean needs to be explicitly exposed.
     */
//    @Bean
//    override fun authenticationManagerBean(): AuthenticationManager {
//        return super.authenticationManagerBean()
//    }
}