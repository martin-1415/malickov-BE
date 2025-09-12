package cz.malickov.backend.config ;

import cz.malickov.backend.service.UserDetailsLoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private JwtFilter jwtFilter;
    private UserDetailsLoginService userDetailsLoginService;
    private int bCryptStrength;

    public SecurityConfig(@Value("${security.bcrypt.strength}") int bCryptStrength, UserDetailsLoginService userDetailsLoginService, JwtFilter jwtFilter){
        this.bCryptStrength=bCryptStrength;
        this.userDetailsLoginService = userDetailsLoginService;
        this.jwtFilter = jwtFilter;
    }

    // @TODO remove swagger-ui.html for security reasons
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf( customizer -> customizer.disable())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/login","/swagger-ui.html")
                            .permitAll()
                        .anyRequest()
                            .authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(bCryptStrength));
        provider.setUserDetailsService(userDetailsLoginService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
      return authenticationConfiguration.getAuthenticationManager();
    }
}
