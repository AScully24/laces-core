package com.laces.core

import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@Component
class StaticContentMvcConfigAdapter : WebMvcConfigurerAdapter() {

    // Allows hot reloading in Intellij. Should not effect live. https://github.com/spring-projects/spring-boot/issues/5133
    /* Cannot be done with application.yml as it prevented swagger.html working.
        https://github.com/springfox/springfox/issues/1460
        https://stackoverflow.com/questions/21123437/how-do-i-use-spring-boot-to-serve-static-content-located-in-dropbox-folder
    */

    override fun addResourceHandlers(registry: ResourceHandlerRegistry?) {
        registry!!
                .addResourceHandler("/**")
                .addResourceLocations("/", "file:src/main/resources/static/", "classpath:src/main/resources/static/")
    }
}
