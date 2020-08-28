package com.example.demo.service.impl;

import com.example.demo.model.XUser;
import com.example.demo.model.XUserDetails;
import com.example.demo.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        XUser xUser = userRepo.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found: "+email));
        return XUserDetails.build(xUser);
    }
}
