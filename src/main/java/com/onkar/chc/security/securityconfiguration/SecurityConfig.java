package com.onkar.chc.security.securityconfiguration;

import com.onkar.chc.security.ownfilter.JwtAuthenticationFilter;
import com.onkar.chc.security.securityexception.JwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*", "http://192.168.*.*:*", "https://*.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain getSecurityFilterChainForJWTSecurity(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(cs -> cs.disable())
                .headers(headers -> headers.frameOptions(f -> f.disable()))
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                // Auth endpoints
                                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/user/signUp").permitAll()
                                .requestMatchers(HttpMethod.GET, "/user/login").permitAll()
                                // Public endpoints (districts list for signup)
                                .requestMatchers("/lab/debug-files").permitAll()
                                .requestMatchers(HttpMethod.GET, "/chc/districts").permitAll()
                                // Role-restricted endpoints
                                .requestMatchers(HttpMethod.POST, "/doctor/createMedicineRecord").hasRole("Doctor")
                                .requestMatchers("/admin/**").hasRole("Admin")
                                // Medical Imaging endpoints
                                .requestMatchers(HttpMethod.POST, "/medical-imaging/upload").hasRole("Doctor")
                                .requestMatchers(HttpMethod.DELETE, "/medical-imaging/**").hasRole("Doctor")
                                .requestMatchers("/medical-imaging/**").authenticated()
                                // Authenticated endpoints (any role)
                                .requestMatchers("/feedback/**").authenticated()
                                .requestMatchers("/chc/search-users").authenticated()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
