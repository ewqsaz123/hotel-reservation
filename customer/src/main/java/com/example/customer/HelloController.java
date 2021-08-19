package com.example.customer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class HelloController {

    @GetMapping
    String HelloWorld(){

        long randomWithMathRandom = (long) (Math.random() * (2000));

        try {
            Thread.sleep(randomWithMathRandom);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

        return "Hello new customer";
    }
    
}