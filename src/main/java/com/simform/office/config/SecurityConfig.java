package com.simform.office.config;

import com.simform.office.filter.JwtFilter;
import com.simform.office.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {
    private final JwtFilter jwtAuthFilter;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;
    }

    // IN-MEMORY AUTHENTICATION
 /*   @Bean
    public UserDetailsService users() {
        UserDetails user = User.builder()
                .username("admin@gmail.com")
                .password(passwordEncoder().encode("Admin@123"))
                .roles("ROLE_ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors().disable().csrf().disable();

/*        http.authorizeHttpRequests()
                .antMatchers("/login", "/resetPassword", "/setPassword").permitAll()
                .antMatchers(HttpMethod.GET, "/users/employees", "users/role/{email}").hasAnyAuthority("ROLE_INTERN", "ROLE_EMPLOYEE", "ROLE_ADMIN")
                .antMatchers(HttpMethod.GET, "/users/{id}").hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_ADMIN")
                .antMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN")
                .antMatchers(HttpMethod.POST, "/users").hasAuthority("ROLE_ADMIN")
                .antMatchers(HttpMethod.PUT, "/users/{id}").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated();*/

        http.authorizeHttpRequests()
                .antMatchers("/login", "/resetPassword", "/setPassword").permitAll()
                .anyRequest().authenticated();


        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authenticationManager(authenticationManager())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
