package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@EnableWebSecurity
//WebSecurityConfigureAdapter - is a convenience class that allows customization to both WebSecurity and HttpSecurity.
public class WebSecurityCustomConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurityCustomConfiguration(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity //allows configuring web based security for specific http requests. By default, it will be applied to all requests, but can be restricted using RequestMatcher or other similar methods.
                .cors().and().csrf().disable()
                .authorizeRequests() //URL interceptor
                .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll()
                .anyRequest().authenticated() //any requests which did not match the previous rules will be passed as long as they are authenticated.
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager())) // AuthenticationManager WebSecurityConfigurerAdapter.authenticationManager()
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //Allows configuring of Session Management.

        httpSecurity.exceptionHandling()    //to handle the case of invalid credentials
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }

    @Override
    @Bean
    // authenticationManagerBean() method can be used to expose the resulting AuthenticationManager as a Bean.
    //Override method 'to expose' the AuthenticationManager built using configure(AuthenticationManagerBuilder) as a Spring bean
    //So, it will be available in the application context
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();  //super - AuthenticationManager WebSecurityConfigurerAdapter.authenticationManagerBean()
    }

    @Override
    //Used by the default implementation of authenticationManager() to attempt to obtain an AuthenticationManager.
    //AuthenticationManagerBuilder would be used only to build a “local” AuthenticationManager, which would be a child of the global one
    //AuthenticationManagerBuilder - a helper class to build an AuthenticationManager
    //                             - coming along with @EnableWebSecurity
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.parentAuthenticationManager(authenticationManagerBean()) //returns AuthenticationManagerBuilder
               .userDetailsService(userDetailsService)
               .passwordEncoder(bCryptPasswordEncoder);
    }
}
