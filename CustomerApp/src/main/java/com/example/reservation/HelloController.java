package com.example.reservation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/CustomerApp")
public class HelloController {

    @GetMapping
    String HelloWorld(){
        return "Hello New 12 CustomerApp";
    }
    
}