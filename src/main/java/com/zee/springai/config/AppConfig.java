package com.zee.springai.config;


import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

/**
 * @dev : Ezekiel Eromosei
 * @date : 12 Apr, 2026
 */

@Configuration
public class AppConfig {

    //to see raw request and response
    // since Spring AI's ChatClient uses Spring RestClient under the covers
    @Bean
    RestClientCustomizer logbookCustomizer(LogbookClientHttpRequestInterceptor logbookInterceptor) {
        return restclient -> restclient.requestInterceptor(logbookInterceptor);
    }
}
