package com.nick404s.dailyfocus.config;


import com.nick404s.dailyfocus.repository.UserRepository;
import com.nick404s.dailyfocus.util.AppRoles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserRepository userRepository, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // the beans to create on startup

    @Bean
    UserDetailsService userDetailsService(){
        return username -> userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public PasswordEncoder passwordEncoder (){
        return new BCryptPasswordEncoder(); // save the password as hash
    }

    // process auth request, verifies the user credentials
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{
        return authConfig.getAuthenticationManager();
    }

    // auth of entry point
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setHeader("WWW-Authenticate", "");
            response.getWriter().write("{\"error\": \"Unauthorized access\"}");
        };
    }

    // add the filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSec) throws Exception {
        httpSec.authorizeHttpRequests(configurer ->
                configurer
                        .requestMatchers( "/api/auth/**", // permit all auth routes
                                // permit swagger endpoints
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**",
                                "/docs").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(AppRoles.ADMIN) // permit admin routes only to the admin users
                        .anyRequest().authenticated() // for all other requests to allow only work with the jwt authentication
        );

        // disable csrf. jwt is stateless
        httpSec.csrf(csrf -> csrf.disable() );

        // handle the exception
        httpSec.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint())); // pass the function

        // make it stateless. no storing cookies. every request is authenticated
        httpSec.sessionManagement( session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // before doing any sping security operations perform the JWT authentication
        httpSec.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSec.build();
    }
}
