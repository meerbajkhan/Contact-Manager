package com.smartcontact.configuration;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    UserDetailsService getUserDetailsService() {
		
		return new UserDetailsServiceImplements();
	}

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider getUserDetailsService = new DaoAuthenticationProvider();
		getUserDetailsService.setUserDetailsService(this.getUserDetailsService());
		getUserDetailsService.setPasswordEncoder(passwordEncoder());
		return getUserDetailsService;

	}

//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.authenticationProvider(daoAuthenticationProvider());
//	}
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}


    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                        .permitAll()
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/user/**")
                        .hasRole("USER")
                        .requestMatchers("/**")
                        .permitAll())
                .formLogin(login -> login
                        .loginPage("/singin")
                        .loginProcessingUrl("/dologin")
                        .defaultSuccessUrl("/user/index"))
                .logout(logout -> logout                                                
                		.logoutUrl("/logout")
                        .logoutSuccessUrl("/singin?logout")
                        .invalidateHttpSession(true)                                
                    );
					
		return httpSecurity.build();
		
		//.failureUrl() login are fail then this Url passing page open
	}

}
