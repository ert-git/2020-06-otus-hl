/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package ru.otus.hl.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import ru.otus.hl.service.UserAuthServiceImpl;

@Configuration
@EnableWebSecurity
@ComponentScan("ru.otus.hl")
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private WebApplicationContext applicationContext;

    public SpringSecurityConfig() {
        super();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return applicationContext.getBean(UserAuthServiceImpl.class);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);

        http
                .formLogin()
                .loginPage("/login.html")
                .failureUrl("/login-error.html")
                .defaultSuccessUrl("/user.html")
                .and()
                .logout()
                .logoutSuccessUrl("/login.html")
                .and()
                .authorizeRequests()
                .antMatchers("/*.css").permitAll()
                .antMatchers("/login.html").permitAll()
                .antMatchers("/registration.html").permitAll()
                .antMatchers("/**").authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403.html")
                .and()
                .addFilterBefore(filter, CsrfFilter.class); // <- this was added;

    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
        // .inMemoryAuthentication()
        // .withUser("jim").password("{noop}demo").roles("ADMIN").and()
        // .withUser("bob").password("{noop}demo").roles("USER").and()
        // .withUser("ted").password("{noop}demo").roles("USER", "ADMIN");
    }

}
