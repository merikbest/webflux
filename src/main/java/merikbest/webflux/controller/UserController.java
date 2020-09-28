package merikbest.webflux.controller;

import merikbest.webflux.configuration.JwtUtil;
import merikbest.webflux.domain.User;
import merikbest.webflux.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity> login(ServerWebExchange serverWebExchange) { // получаем форму логина и авторизации
        return serverWebExchange.getFormData().flatMap(credentials ->
                userService.findByUsername(credentials.getFirst("username"))
                        .cast(User.class)
                        .map(userDetails ->
                                Objects.equals(
                                        credentials.getFirst("password"),
                                        userDetails.getPassword()
                                )
                                        ? ResponseEntity.ok(jwtUtil.generateTokenUser(userDetails))
                                        : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                        ).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
        ); // преобразуем форму в ответ
    }

}
