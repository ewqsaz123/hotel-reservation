package com.example.hotel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hotel")
public class HelloController {

    @GetMapping
    String HelloWorld(){
        return "Hello new  11 hotel";
    }
    
}
