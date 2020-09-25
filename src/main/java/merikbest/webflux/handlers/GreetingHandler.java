package merikbest.webflux.handlers;

import merikbest.webflux.domain.Message;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class GreetingHandler {
    // В всех функциональных обертках для HTTP запросов будут фигурировать ServerRequest и ServerResponse (вместо HttpRequest HttpResponse)
    public Mono<ServerResponse> hello(ServerRequest request) { // получили запрос
//        BodyInserter<String, ReactiveHttpOutputMessage> body =
//                BodyInserters.fromValue("Hello, Spring!"); // BodyInserters - класс для генерации контента, который возвращаем пользователю

        Long start = request.queryParam("start")
                .map(Long::valueOf)
                .orElse(0L);

        Long count = request.queryParam("count")
                .map(Long::valueOf)
                .orElse(3L);

        Flux<Message> data = Flux
                .just(
                        "Hello React!",
                        "More then one",
                        "Third post",
                        "Fourth post",
                        "Fifth post"
                )
                .skip(start) // пропустить элементы до значения start
                .take(count) // вывод количества элементов согласно count
                .map(Message::new);

        return ServerResponse // ответ
                .ok() // 200
                .contentType(MediaType.APPLICATION_JSON) // заголовок APPLICATION_JSON
                .body(data, Message.class); // тело запроса
    }

    public Mono<ServerResponse> index(ServerRequest request) {
        String user = request.queryParam("user")
                .orElse("Nobody");

        return ServerResponse
                .ok()
                .render("index", Map.of("user", user));
    }
}
