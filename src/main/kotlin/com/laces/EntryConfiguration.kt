package com.laces

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@ComponentScan
@EntityScan
@EnableJpaRepositories
@SpringBootConfiguration
class EntryConfiguration