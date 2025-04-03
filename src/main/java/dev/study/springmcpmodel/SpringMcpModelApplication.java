package dev.study.springmcpmodel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class SpringMcpModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMcpModelApplication.class, args);
    }

}
