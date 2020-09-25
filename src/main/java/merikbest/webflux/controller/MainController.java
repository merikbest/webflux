package merikbest.webflux.controller;

import merikbest.webflux.domain.Message;
import merikbest.webflux.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/controller")
public class MainController {
    private final MessageService messageService;

    @Autowired
    public MainController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public Flux<Message> listOfMessages(
            @RequestParam(defaultValue = "0") Long start,
            @RequestParam(defaultValue = "3") Long count
    ) {
        return messageService.list();
    }

    @PostMapping
    public Mono<Message> addMessage(@RequestBody Message message) {
        return messageService.addOne(message);
    }
}
/*
    await(
            await fetch(
            '/controller',
    {
        method: 'POST',
                headers: { 'Content-type': 'application/json' },
        body: JSON.stringify({ data : 'one'})
    }
    )
            ).json()

*/
