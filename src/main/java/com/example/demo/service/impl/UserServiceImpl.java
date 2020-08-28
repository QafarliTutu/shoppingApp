package com.example.demo.service.impl;

import com.example.demo.config.JwtTokenUtil;
import com.example.demo.enums.Language;
import com.example.demo.enums.RoleEnum;
import com.example.demo.model.Role;
import com.example.demo.model.XUser;
import com.example.demo.model.XUserDetails;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.SignUpRequest;
import com.example.demo.payload.response.JwtResponse;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.repository.RoleRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public ResponseEntity<?> save(SignUpRequest signUpRequest, HttpServletRequest request) {
        log.info(String.format("User %s is trying to register. User - %s >>> User IP - %s", signUpRequest.getEmail(), signUpRequest.getEmail(), request.getRemoteAddr()));
        if (userRepo.existsByEmail(signUpRequest.getEmail())) {
            log.info(String.format("Error: Email is already in use. User - %s >>> User IP - %s", signUpRequest.getEmail(), request.getRemoteAddr()));
            return ResponseEntity.badRequest().body(new MessageResponse(false, 1, "Error: Email is already in use."));
        }
        if (userRepo.existsByMobNumber(signUpRequest.getMobNumber())) {
            log.info(String.format("Error: Mobile Number is already in use. User - %s >>> User IP - %s", signUpRequest.getEmail(), request.getRemoteAddr()));
            return ResponseEntity.badRequest().body(new MessageResponse(false, 2, "Error: Mobile Number is already in use."));
        }
        if (!signUpRequest.getPassword().equals(signUpRequest.getPasswordConfirm())) {
            log.info(String.format("Error: Confirm Password doesn't match with Password. User - %s >>> User IP - %s", signUpRequest.getEmail(), request.getRemoteAddr()));
            return ResponseEntity.badRequest().body(new MessageResponse(false, 3, "Error: Confirm Password doesn't match with Password."));

        }
        XUser xUser = XUser.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .mobNumber(signUpRequest.getMobNumber())
                .status(false)
                .build();
        if(signUpRequest.getLanguage() == null){
            xUser.setLanguage(Language.EN);
        }
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if(strRoles == null){
            Role xUserRole = roleRepo.findByName(RoleEnum.ROLE_USER)
                    .orElseThrow(()->new RuntimeException("Error: Role is not found."));
            roles.add(xUserRole);
        }else {
            strRoles.forEach(role ->{
                switch (role){

                    case "admin":
                        Role adminRole = roleRepo.findByName(RoleEnum.ROLE_ADMIN)
                                .orElseThrow(()->new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "moderator":
                        Role modRole = roleRepo.findByName(RoleEnum.ROLE_MODERATOR)
                                .orElseThrow(()->new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepo.findByName(RoleEnum.ROLE_USER)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                        break;
                }
            });
        }
        xUser.setRoles(roles);
        userRepo.save(xUser);
        log.info(String.format("Success: User successfully registered. User - %s >>> User IP - %s", signUpRequest.getEmail(), request.getRemoteAddr()));
        return ResponseEntity.ok().body(new MessageResponse(true, 3, "Success: User successfully registered."));
    }

    @Override
    public ResponseEntity<?> login(LoginRequest loginRequest, HttpServletRequest request) {
        log.info("what about here?");
        List<GrantedAuthority> grantedAuths = new ArrayList<>();


        //validate and do your additionl logic and set the role type after your validation. in this code i am simply adding admin role type
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER" ));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword(), grantedAuths)
        );
        log.info("i am here");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generateToken(authentication);
        XUserDetails xUserDetails = (XUserDetails) authentication.getPrincipal();
        List<String> roles = xUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        log.info("here");
        return ResponseEntity.ok(
                JwtResponse.builder()
                .id(xUserDetails.getId())
                .name(xUserDetails.getName())
                .email(xUserDetails.getEmail())
                .mobNumber(xUserDetails.getMobNumber())
                .language(xUserDetails.getLanguage())
                .jwtToken(jwt)
                .type("Bearer")
                .roles(roles)
                .build());
    }

}

