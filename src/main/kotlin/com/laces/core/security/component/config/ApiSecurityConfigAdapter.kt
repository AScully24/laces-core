package com.laces.core.security.component.config

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

//@Configuration
//@Order(1)
class ApiSecurityConfigAdapter{
    // https://stackoverflow.com/questions/35890540/when-to-use-spring-securitys-antmatcher
    fun configure(http: HttpSecurity) {
        http.antMatcher("/api/**")
                .authorizeRequests()
                .anyRequest()
                .authenticated()
            .and()
                .antMatcher("/api/transform/**")
                .authorizeRequests()
                .anyRequest()
                .permitAll()
    }
}