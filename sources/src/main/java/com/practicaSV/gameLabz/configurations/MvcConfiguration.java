package com.practicaSV.gameLabz.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaSV.gameLabz.controllers.AuthenticationInterceptor;
import com.practicaSV.gameLabz.controllers.LoggingInterceptor;
import com.practicaSV.gameLabz.utils.JsonViews;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class MvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor).excludePathPatterns(PathConstants.LOGIN_PATH, PathConstants.USERS_PATH, PathConstants.SHARED_LINK_ID_PATH);
        registry.addInterceptor(new LoggingInterceptor());
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(converter());
        addDefaultHttpMessageConverters(converters);

    }

    @Bean
    MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setConfig(mapper.getSerializationConfig().withView(JsonViews.Default.class));

        converter.setObjectMapper(mapper);

        return converter;
    }
}
