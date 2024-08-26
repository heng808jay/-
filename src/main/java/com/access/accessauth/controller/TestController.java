package com.access.accessauth.controller;

import com.access.accessauth.service.ApplyJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ApplyJobService applyJobService;

    @GetMapping("/test")
    public String test(){
        return "123";
    }
}
