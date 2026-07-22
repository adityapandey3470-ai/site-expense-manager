package com.aditya.siteexpensemanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Supervisor-only write actions
                        .requestMatchers("/attendances/**").hasAnyAuthority(
                                "ROLE_SUPERVISOR", "ROLE_OPERATIONS", "ROLE_ACCOUNTS", "ROLE_DIRECTOR")
                        .requestMatchers("/travel-expenses/**").hasAnyAuthority(
                                "ROLE_SUPERVISOR", "ROLE_OPERATIONS", "ROLE_ACCOUNTS", "ROLE_DIRECTOR")
                        .requestMatchers("/requests/**").hasAnyAuthority(
                                "ROLE_SUPERVISOR", "ROLE_OPERATIONS", "ROLE_ACCOUNTS", "ROLE_DIRECTOR")

                        // Payout / accounts specific
                        .requestMatchers("/payouts/**").hasAnyAuthority(
                                "ROLE_ACCOUNTS", "ROLE_DIRECTOR")

                        // File uploads — any authenticated user can upload a bill
                        .requestMatchers("/files/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        // Sites — only Operations/Director manage sites
                        .requestMatchers(HttpMethod.GET, "/sites/**").hasAnyAuthority(
                                "ROLE_SUPERVISOR", "ROLE_OPERATIONS", "ROLE_ACCOUNTS", "ROLE_DIRECTOR")
                        .requestMatchers("/sites/**").hasAnyAuthority(
                                "ROLE_OPERATIONS", "ROLE_DIRECTOR")

                        // Ledger — read allowed to all, write/delete restricted to Accounts/Director
                        .requestMatchers(HttpMethod.GET, "/ledgers/**").hasAnyAuthority(
                                "ROLE_SUPERVISOR", "ROLE_OPERATIONS", "ROLE_ACCOUNTS", "ROLE_DIRECTOR")
                        .requestMatchers("/ledgers/**").hasAnyAuthority(
                                "ROLE_ACCOUNTS", "ROLE_DIRECTOR")

                        // Everything else needs a valid token
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                        .addFilterBefore(rateLimitFilter, JwtAuthFilter.class);


        return http.build();
    }
}
