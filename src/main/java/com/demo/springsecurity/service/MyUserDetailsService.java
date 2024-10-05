package com.demo.springsecurity.service;

import com.demo.springsecurity.entity.UserPrinciple;
import com.demo.springsecurity.entity.Users;
import com.demo.springsecurity.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = userRepo.findByUsername(username);

        if(null == user) {
            throw new UsernameNotFoundException("You are not registered with us.");
        }

        return new UserPrinciple(user);

//        return User.withUsername(user.getUsername())
//                .password(user.getPassword())
//                .build();
    }
}
