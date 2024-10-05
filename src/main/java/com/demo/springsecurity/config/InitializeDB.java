package com.demo.springsecurity.config;

import com.demo.springsecurity.entity.Users;
import com.demo.springsecurity.repo.UserRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitializeDB {

    @Autowired
    private UserRepo userRepo;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A, 12);

    @PostConstruct
    public void init() {
        userRepo.saveAll(List
                .of(
                        Users.builder()
                                .id(1L)
                                .name("Kuldeep")
                                .username("kd")
                                .password(encoder.encode("kd"))
                                .build(),
                        Users.builder()
                                .id(2L)
                                .name("Test")
                                .username("test")
                                .password(encoder.encode("test"))
                                .build()
                ));
    }
}
