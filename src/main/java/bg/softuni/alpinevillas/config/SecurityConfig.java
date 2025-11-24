package bg.softuni.alpinevillas.config;

import bg.softuni.alpinevillas.entities.User;
import bg.softuni.alpinevillas.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/villas", "/villas/**",
                                "/css/**", "/js/**", "/images/**", "/register").permitAll()
                        .requestMatchers("/bookings/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/villas", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

                return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository users) {
        return username -> {
            User u = users.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            List<SimpleGrantedAuthority> authorities =
                    u.getRoles().stream()
                            .map(r -> "ROLE_" + r.getName())
                            .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                            .toList();

            return org.springframework.security.core.userdetails.User
                    .withUsername(u.getUsername())
                    .password(u.getPassword())
                    .disabled(u.isBlocked())
                    .authorities(authorities)
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
