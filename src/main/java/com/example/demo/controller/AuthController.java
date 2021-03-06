package com.example.demo.controller;

import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.SignUpRequest;
import com.example.demo.service.impl.UserServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    @Autowired
    private  UserServiceImpl userService;


    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletRequest request){
        return userService.signUp(signUpRequest,request);
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request){
        return userService.login(loginRequest,request);
    }

//    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
//        return userService.logout(request,response,authentication);
//    }


}
