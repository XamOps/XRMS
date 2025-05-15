package com.XAMMER.HRMS.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;



// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Autowired
//     private UserDetailsService userDetailsService;

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//             .authorizeHttpRequests()
//                 .requestMatchers("/custom-login", "/css/**", "/js/**").permitAll()
//                 .anyRequest().authenticated()
//                 .and()
//             .formLogin()
//                 .loginPage("/custom-login")
//                 .loginProcessingUrl("/perform_login")
//                 .defaultSuccessUrl("/dashboard", true) // 🚨 Force redirect to dashboard after login
//                 .failureUrl("/custom-login?error=true")
//                 .permitAll()
//                 .and()
//             .logout()
//                 .logoutUrl("/logout")
//                 .logoutSuccessUrl("/custom-login?logout=true")
//                 .permitAll();

//         return http.build();
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//         return http.getSharedObject(AuthenticationManagerBuilder.class)
//                 .userDetailsService(userDetailsService)
//                 .passwordEncoder(passwordEncoder())
//                 .and()
//                 .build();
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return NoOpPasswordEncoder.getInstance(); // ⚠️ Replace with bcrypt for production
//     }
// }


import org.springframework.security.core.authority.AuthorityUtils;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Lazy
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler; // Inject the custom handler

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests(authorize ->
        authorize
.requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**", "/sounds/**").permitAll()            //.requestMatchers("/dashboard").authenticated()
            .requestMatchers("/dashboard").hasAnyRole("USER","USER_MANAGER")
            .requestMatchers("/manager/dashboard").hasRole("USER_MANAGER") // Secure manager dashboard

            .requestMatchers("/admin/dashboard").hasRole("ADMIN")
            .requestMatchers("/admin/attendance/reset/**").hasRole("ADMIN") // ✅ Add this line
            .anyRequest().authenticated()
    )
    
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .successHandler(customAuthenticationSuccessHandler) // Use the injected handler
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}