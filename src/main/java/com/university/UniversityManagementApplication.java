package com.university;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Main Spring Boot Application.
 * Excludes default DataSourceAutoConfiguration and HibernateJpaAutoConfiguration
 * because multi-database configurations explicitly manage their own DataSource,
 * EntityManagerFactory, and PlatformTransactionManager beans.
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class UniversityManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniversityManagementApplication.class, args);
    }
}
