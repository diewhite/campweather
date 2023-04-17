package com.example.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.example.oauth2.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{
	
	@Autowired
	CustomOAuth2UserService customOAuth2UserService;
	
	@Bean                                                            
	public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
		http	
				.csrf().disable()
				.headers().frameOptions().disable()
				.and()
				.authorizeHttpRequests()
				.antMatchers("/","/css/**","/images/**","/h2-console/*","/h2**").permitAll()
				.antMatchers("/login","/sign**").permitAll()
				.antMatchers("/user/**").hasRole("USER")
				.anyRequest().authenticated()
				.and()
				.exceptionHandling().accessDeniedPage("/accessDenied")
				.and()
				.logout().logoutUrl("/logout")
				.logoutSuccessUrl("/").permitAll()
				.and()
				.oauth2Login()
				.loginPage("/login")
				.userInfoEndpoint()
				.userService(customOAuth2UserService);
		return http.build();
				
	}
}
