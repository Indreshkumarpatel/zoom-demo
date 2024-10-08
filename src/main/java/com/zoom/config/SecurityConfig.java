package com.zoom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select name,password ,true from users where name = ?"
        );
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select username,role from roles where username=?"
        );
        return jdbcUserDetailsManager;
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
                                configurer
                                        .requestMatchers("/**").permitAll()
                                        .requestMatchers("/joinMeeting/**").permitAll()
                                        .requestMatchers("/user/login").permitAll()
                                        .requestMatchers("/user/signUp").permitAll()
                                        .requestMatchers("/images/**", "/css/**", "/js/**").permitAll()  // Allow access to static resources
//                                .requestMatchers(HttpMethod.POST,"/api/sessions").permitAll()
//                                .requestMatchers(HttpMethod.POST,"/api/sessions/**").permitAll()
                                        .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                .loginPage("/user/login")
                                .loginProcessingUrl("/authenticateTheUser")
                                .defaultSuccessUrl("/userDashboard")
                                .permitAll()
                )
                .logout(logout -> logout.permitAll()
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        System.out.println("Security configuration applied successfully.");
        return http.build();
    }
}


