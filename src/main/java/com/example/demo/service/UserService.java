package com.example.demo.service;

import com.example.demo.payload.request.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public interface UserService {

    ResponseEntity<?> login (LoginRequest loginRequest, HttpServletRequest request);

}
