package merikbest.webflux.configuration;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class AuthenticationManager implements ReactiveAuthenticationManager { //AuthenticationManager - обслуживает логику авторизации
    private final JwtUtil jwtUtil; // JwtUtil "складываем" логику

    @Autowired
    public AuthenticationManager(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        // из authentication получаем Credentials пользователя
        String authToken = authentication.getCredentials().toString();

        String username;

        try { // оборачиваем в try так как возможны ошибки (чего-то не хватает, нет токена и т.д.)
            username = jwtUtil.extractUsername(authToken); // расшифруем  токен
        } catch (Exception e) {
            username = null;
            System.out.println(e);
        }

        if (username != null && jwtUtil.validateToken(authToken)) { // проверка пользователя и токена
            // получить все поля токена
            Claims claims = jwtUtil.getClaimsFromToken(authToken);
            // получить из токена роли пользователя в виде List.class
            List<String> role = claims.get("role", List.class);
            // переобразование роли, так-как роли в виде строки не подходят
            List<SimpleGrantedAuthority> authorities = role.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // получаем authentication token который нужен спрингу
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );
            // Возвращаем аутентифицированного пользователя
            return Mono.just(authenticationToken);
        } else {
            // Если нет пользователя возвращаем пустой Mono объект
            return Mono.empty();
        }
    }
}
