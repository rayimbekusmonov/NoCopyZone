package com.rayimbek.nocopyzone.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.rayimbek.nocopyzone.security.JwtAuthFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // JwtAuthFilter EMAS — faqat UserDetailsService inject qilinadi
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC
                        .requestMatchers("/auth/**").permitAll()

                        // ADMIN ONLY
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/groups").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/groups/*/students/*").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/groups/*/students/*").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/groups/*/courses/*").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/groups/*/courses/*").hasAnyRole("ADMIN")

                        // TEACHER + ADMIN
                        .requestMatchers(HttpMethod.POST, "/courses").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/courses/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/courses/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/tasks").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/tasks/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/tasks/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/lectures/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/lectures/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/lectures/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/quiz/task/*/questions").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/quiz/questions/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/quiz/task/*/questions/admin").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/submissions/*/grade").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/submissions/task/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/enrollments/course/*/student/*").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/enrollments/course/*/group/*").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/enrollments/course/*/student/*").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/enrollments/course/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/proctor/task/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/students").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/teachers").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").hasAnyRole("TEACHER", "ADMIN")

                        // STUDENT ONLY
                        .requestMatchers(HttpMethod.POST, "/submissions/start/**").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/submissions/*/submit").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/submissions/my").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/quiz/task/*/questions").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/quiz/submission/*/answer").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/quiz/submission/*/finish").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/groups/my-courses").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/proctor/event").hasRole("STUDENT")

                        // ALL AUTHENTICATED
                        .requestMatchers(HttpMethod.GET, "/courses/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/tasks/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/lectures/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/groups/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/enrollments/my").authenticated()
                        .requestMatchers(HttpMethod.GET, "/proctor/submission/**").authenticated()
                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers("/users/change-password").authenticated()
                        .requestMatchers("/files/**").authenticated()

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:3001",
                "https://rayimbek.uz",
                "https://www.rayimbek.uz",
                "https://no-copy-zone-front-end.vercel.app"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
