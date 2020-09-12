package com.example.demo.controller;

import com.example.demo.service.impl.UserServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/upd")
@RestController
@Log4j2
public class UpdateController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody Map<String,String> updateRequest){
        return userService.update(updateRequest);
    }

    @PostMapping("/activateStatus")
    public ResponseEntity<?> activate(@RequestParam("token") String token){
        return userService.activate(token);
    }

}
