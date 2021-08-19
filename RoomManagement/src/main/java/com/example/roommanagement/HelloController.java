package com.example.roommanagement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roommanagement")
public class HelloController {

    @GetMapping
    String HelloWorld(){
        return "Hello new room management";
    }
    
}
