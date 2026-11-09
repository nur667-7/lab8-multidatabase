package com.university.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.university.grade.repository",
        entityManagerFactoryRef = "gradeEntityManagerFactory",
        transactionManagerRef = "gradeTransactionManager"
)
public class GradeDatabaseConfig {

    private final Environment env;

    public GradeDatabaseConfig(Environment env) {
        this.env = env;
    }

    @Bean(name = "gradeDataSourceProperties")
    @ConfigurationProperties("spring.datasource.grade")
    public DataSourceProperties gradeDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "gradeDataSource")
    public DataSource gradeDataSource(
            @Qualifier("gradeDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "gradeEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean gradeEntityManagerFactory(
            @Qualifier("gradeDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.university.grade.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto", "update"));
        properties.put("hibernate.show_sql", env.getProperty("spring.jpa.show-sql", "true"));
        properties.put("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql", "true"));
        if (env.containsProperty("spring.jpa.properties.hibernate.dialect")) {
            properties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        }
        em.setJpaPropertyMap(properties);
        em.setPersistenceUnitName("gradePersistenceUnit");

        return em;
    }

    @Bean(name = "gradeTransactionManager")
    public PlatformTransactionManager gradeTransactionManager(
            @Qualifier("gradeEntityManagerFactory") LocalContainerEntityManagerFactoryBean gradeEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(gradeEntityManagerFactory.getObject()));
    }
}
