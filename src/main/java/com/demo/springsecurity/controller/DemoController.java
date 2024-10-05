package com.demo.springsecurity.controller;

import com.demo.springsecurity.entity.Users;
import com.demo.springsecurity.repo.UserRepo;
import com.demo.springsecurity.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class DemoController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @GetMapping("test")
    public String doTest(HttpServletRequest request) {
        return "The test endpoint is successful: " + request.getSession().getId();
    }

    @GetMapping("csrf")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @PostMapping("test")
    public String doPostTest(HttpServletRequest request) {
        return "The test endpoint is successful: " + request.getSession().getId();
    }

    @PostMapping("login")
    public String login(@RequestBody Users user) {
        return userService.verify(user);
    }

    @GetMapping("users")
    public List<Users> getUsers() {
        return userRepo.findAll();
    }
}
