package com.laces.core.database

import com.laces.form.core.PackageLocations
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource


@Configuration
@EntityScan
class MultiDatabaseConfiguration(
    private val packageLocations: PackageLocations
) {

    @Bean(name = ["masterDatabaseConfig"])
    @ConfigurationProperties(prefix = "laces.datasources.master")
    fun masterConfiguration(): HikariConfig {
        return HikariConfig()
    }

    @Bean(name = ["slaveDatabaseConfig"])
    @ConfigurationProperties(prefix = "laces.datasources.slave")
    @ConditionalOnProperty("laces.datasources.slave.enabled")
    fun slaveConfiguration(): HikariConfig {
        return HikariConfig()
    }

    @Bean
    fun routingDataSource(
        masterDatabaseConfig: HikariConfig,
        slaveDatabaseConfig: HikariConfig?
    ): DataSource {
        return MasterReplicaRoutingDataSource(
            HikariDataSource(masterDatabaseConfig),
            HikariDataSource(slaveDatabaseConfig)
        )

    }

    @Bean
    fun entityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        routingDataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        val packages = packageLocations.packages
            .toMutableList()
            .apply { add("com.adt") }
            .toSet()
            .toTypedArray()
        return builder
            .dataSource(routingDataSource)
            .packages(*packages)
            .properties(jpaProperties(hibernateProperties()))
            .build()
    }

    @Bean
    @Primary
    fun transactionManager(@Qualifier("jpaTxManager") wrapped: PlatformTransactionManager): PlatformTransactionManager {
        return ReplicaAwareTransactionManager(wrapped)
    }

    @Bean(name = ["jpaTxManager"])
    fun jpaTransactionManager(emf: EntityManagerFactory): PlatformTransactionManager {
        return JpaTransactionManager(emf)
    }

    @Bean
    fun entityManagerFactoryBuilder(): EntityManagerFactoryBuilder? {
        return EntityManagerFactoryBuilder(HibernateJpaVendorAdapter(), mutableMapOf<String, String>(), null)
    }

    @Bean
    @ConfigurationProperties(prefix = "laces.datasources.hibernate")
    fun hibernateProperties(): LacesHibernateProperties {
        return LacesHibernateProperties()
    }

    data class LacesHibernateProperties(var ddlAuto: String = "none")

    protected fun jpaProperties(properties: LacesHibernateProperties): Map<String, Any>? {
        val props: MutableMap<String, Any> = HashMap()
        props["hibernate.physical_naming_strategy"] = SpringPhysicalNamingStrategy::class.java.name
        props["hibernate.implicit_naming_strategy"] = SpringImplicitNamingStrategy::class.java.name
        props["hibernate.hbm2ddl.auto"] = properties.ddlAuto
        return props
    }
}