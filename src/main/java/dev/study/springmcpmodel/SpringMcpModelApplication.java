package dev.study.springmcpmodel;

import dev.study.springmcpmodel.tool.RedisStaticsTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class SpringMcpModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMcpModelApplication.class, args);
    }

    @Bean
    public List<ToolCallback> redisTools(RedisStaticsTool redisStaticsTool){
        return List.of(ToolCallbacks.from(redisStaticsTool));
    }
}
