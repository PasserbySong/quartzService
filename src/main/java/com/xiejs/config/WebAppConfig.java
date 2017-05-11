package com.xiejs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Administrator on 2016/3/31.
 */
@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {

    @Bean(name="restTemplate")
    public RestTemplate restTemplate(){
        ClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        return new RestTemplate(clientHttpRequestFactory);
    }

}
