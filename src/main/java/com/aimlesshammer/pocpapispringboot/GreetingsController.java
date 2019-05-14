package com.aimlesshammer.pocpapispringboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingsController {

    @GetMapping("/greeting")
    public String greeting() {
        return "Hello World! :)";
    }
}
