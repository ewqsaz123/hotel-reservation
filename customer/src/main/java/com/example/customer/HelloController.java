package com.example.customer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class HelloController {

    @GetMapping
    String HelloWorld(){
        return "Hello new 1 customer";
    }
    
}