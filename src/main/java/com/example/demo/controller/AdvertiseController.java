package com.example.demo.controller;

import com.example.demo.config.JwtTokenService;
import com.example.demo.model.Category;
import com.example.demo.model.Item;
import com.example.demo.model.SubCategory;
import com.example.demo.model.XUser;
import com.example.demo.payload.request.AdvertiseRequest;
import com.example.demo.repository.CategoryRepo;
import com.example.demo.repository.ItemRepo;
import com.example.demo.repository.SubCategoryRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.impl.AdvertiseService;
import com.example.demo.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/adv")
@RequiredArgsConstructor
public class AdvertiseController {

    @Autowired
    private AdvertiseService advService;

    @PostMapping("/post")
    public ResponseEntity<?> postAdv(@Valid @RequestBody AdvertiseRequest advRequest, HttpServletRequest request) {
        String jwt = getToken(request);
        return advService.postAdv(advRequest, jwt);
    }

    private String getToken(HttpServletRequest request) {
        String jwt = null;
        String header = request.getHeader("Authorization");
        if(StringUtils.hasText(header) && header.startsWith("Bearer ")){
            jwt = header.substring(7, header.length());
        }
        return jwt;
    }
}
