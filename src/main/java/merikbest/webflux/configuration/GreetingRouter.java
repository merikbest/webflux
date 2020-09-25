package merikbest.webflux.configuration;

import merikbest.webflux.handlers.GreetingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration // Определяем поведение спринга
public class GreetingRouter {

    // определяет маппинг пользовательских запросов по URL "/hello",
    // на какие-то ответы которые пользователь будет получать
    @Bean
    public RouterFunction<ServerResponse> route(GreetingHandler greetingHandler) {
        RequestPredicate route = RequestPredicates
                .GET("/hello") // метод и путь (@GetMapping)
                .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)); // Принимаем запрос с заголовком TEXT_PLAIN

        return RouterFunctions
                .route(route, greetingHandler::hello)
                .andRoute( // главная страница
                        RequestPredicates.GET("/"),
                        greetingHandler::index
                );
    }
}
