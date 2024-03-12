package com.forlks.kssecurityexam.common.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource


@Configuration
@EnableJpaRepositories(
        basePackages = ["com.forlks.kssecurityexam.users.repository", "com.forlks.kssecurityexam.transfers.repository"],
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
class DataSourceConfig (
        private val jpaProperties: JpaProperties,
        private val hibernateProperties: HibernateProperties
){

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    fun dataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @Primary
    fun dataSource(): DataSource {
        return dataSourceProperties()
                .initializeDataSourceBuilder()
                .build()
    }

    @Bean
    @Primary
    fun entityManagerFactory(
            builder: EntityManagerFactoryBuilder
    ): LocalContainerEntityManagerFactoryBean {
        val properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.properties, HibernateSettings()
        )

        return builder.dataSource(dataSource())
                .packages("com.forlks.kssecurityexam.users.entity", "com.forlks.kssecurityexam.transfers.entity")
                .persistenceUnit("entityManager")
                .properties(properties)
                .build()
    }

    @Bean
    @Primary
    fun transactionManager(
            @Qualifier(value = "entityManagerFactory")  entityManagerFactory : LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        var transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory.`object`

        return transactionManager
    }
}
