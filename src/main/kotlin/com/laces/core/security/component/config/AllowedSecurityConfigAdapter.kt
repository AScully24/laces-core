package com.laces.core.security.component.config

import org.springframework.beans.factory.annotation.Autowired
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


@Configuration
class AllowedSecurityConfigAdapter : WebSecurityConfigurerAdapter(){

    @Autowired
    lateinit var authenticationProvider : DaoAuthenticationProvider

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers("/**", "/built/**", "/*.js", "/*.jsx","/*.jpg", "/main.css"
                    ,"/auth/**","/h2-console/**","/swagger.html","/swagger-ui.html","/swagger-resources/**",
                    "/v2/**","/webjars/**", "/register-confirmation/**","/subscription/**")
                .permitAll()
            .and()
                .formLogin()
                .permitAll()
                .successForwardUrl("/auth/success")
                .failureForwardUrl("/auth/failure")
            .and()
                .logout()
                .permitAll()
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

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(authenticationProvider)
    }

    @Bean
    fun sessionRegistry(): SessionRegistry {
        return SessionRegistryImpl()
    }
}