package com.asarao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/*
 * @ClassName: WebMvcConfiguration
 * @Description: TODO
 * @Author: Asarao
 * @Date: 2020/6/4 11:54
 * @Version: 1.0
 **/
@Configuration
public class WebMvcConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        super.configure(http);
    }
}
