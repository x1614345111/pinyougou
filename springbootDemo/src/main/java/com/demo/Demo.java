package com.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Demo {

    @RequestMapping("/info")
    public String info(){
        return "hello world !!";
    }
}
