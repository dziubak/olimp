package com.parse.olimp.config;

import com.parse.olimp.entity.SportUrl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SportUrlConfig {

    @Bean
    @ConfigurationProperties(prefix = "url.sport")
    public SportUrl sportUrl() {
        return new SportUrl();
    }

}
