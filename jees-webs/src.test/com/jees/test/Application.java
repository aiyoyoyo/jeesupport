package com.jees.test;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ComponentScan("com.jees")
@DependsOn({"commonContextHolder", "commonConfig"})
public class Application {
    public static void main(String[] args) throws Exception {
        SpringApplication.run( Application.class, args);
    }
}
