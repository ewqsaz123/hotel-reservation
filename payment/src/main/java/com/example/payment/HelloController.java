package com.example.payment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class HelloController {

    @GetMapping
    String HelloWorld(){
        return "Hello new payment";
    }
    
}