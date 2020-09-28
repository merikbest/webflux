package merikbest.webflux.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity // Подключение "реактивных" Security
public class WebSecurityConfig {
    private final SecurityContextRepository securityContextRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public WebSecurityConfig(SecurityContextRepository securityContextRepository, AuthenticationManager authenticationManager) {
        this.securityContextRepository = securityContextRepository;
        this.authenticationManager = authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // Только для примера. Необходимо использовать BCryptPasswordEncoder
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .exceptionHandling() // убрать базовые окна авторизации
                .authenticationEntryPoint(
                        (swe, e) -> Mono.fromRunnable(
                                () -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)
                        )
                )
                .accessDeniedHandler(
                        (swe, e) -> Mono.fromRunnable(
                                () -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)
                        )
                )
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager) // аутентификация
                .securityContextRepository(securityContextRepository) // вытаскиваем информацию из json токена
                .authorizeExchange()
                .pathMatchers("/", "login", "/favicon.ico").permitAll()
                .pathMatchers("/controller").hasRole("ADMIN")
                .anyExchange().permitAll()
                .and()
                .build();
    }
}
