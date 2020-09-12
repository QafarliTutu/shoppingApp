package com.example.demo.service.impl;

import com.example.demo.config.JwtTokenService;
import com.example.demo.enums.Language;
import com.example.demo.enums.RoleEnum;
import com.example.demo.model.ActivationToken;
import com.example.demo.model.Role;
import com.example.demo.model.XUser;
import com.example.demo.model.XUserDetails;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.SignUpRequest;
import com.example.demo.payload.response.JwtResponse;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.repository.ActivationTokenRepo;
import com.example.demo.repository.RoleRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
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
    private JwtTokenService jwtTokenUtil;

    @Autowired
    private EmailSenderService senderService;

    @Autowired
    private ActivationTokenRepo tokenRepo;


    public ResponseEntity<?> signUp(SignUpRequest signUpRequest, HttpServletRequest request) {
        log.info("User {} is trying to register. User - {} >>> User IP - {}", signUpRequest.getEmail(), signUpRequest.getEmail(), request.getRemoteAddr());
        if (userRepo.existsByEmail(signUpRequest.getEmail())) {
            log.warn("Error: Email is already in use. User - {} >>> User IP - {}", signUpRequest.getEmail(), request.getRemoteAddr());
            return ResponseEntity.badRequest().body(new MessageResponse(false, 1, "Error: Email is already in use."));
        }
        if (userRepo.existsByMobNumber(signUpRequest.getMobNumber())) {
            log.warn("Error: Mobile Number is already in use. User - {} >>> User IP - {}", signUpRequest.getEmail(), request.getRemoteAddr());
            return ResponseEntity.badRequest().body(new MessageResponse(false, 2, "Error: Mobile Number is already in use."));
        }
        if (!signUpRequest.getPassword().equals(signUpRequest.getPasswordConfirm())) {
            log.warn("Error: Confirm Password doesn't match with Password. User - {} >>> User IP - {}", signUpRequest.getEmail(), request.getRemoteAddr());
            return ResponseEntity.badRequest().body(new MessageResponse(false, 3, "Error: Confirm Password doesn't match with Password."));

        }
        XUser xUser = XUser.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .mobNumber(signUpRequest.getMobNumber())
                .language(checkLanguage(signUpRequest.getLanguage()))
                .status(false)
                .build();
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role xUserRole = roleRepo.findByName(RoleEnum.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(xUserRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepo.findByName(RoleEnum.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "moderator":
                        Role modRole = roleRepo.findByName(RoleEnum.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepo.findByName(RoleEnum.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                        break;
                }
            });
        }
        xUser.setRoles(roles);
        userRepo.save(xUser);
        log.info("Success: User successfully registered. User - {} >>> User IP - {}", signUpRequest.getEmail(), request.getRemoteAddr());
        sendEmail(xUser);
        log.info("Success: Activation email was sent successfully. User - {} >>> User IP - {}", signUpRequest.getEmail(), request.getRemoteAddr());
        return ResponseEntity.ok().body(new MessageResponse(true, 3, "Success: User successfully registered."));
    }

    private void sendEmail(XUser xUser) {
        ActivationToken token = new ActivationToken(xUser);
        tokenRepo.save(token);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(xUser.getEmail());
        mailMessage.setSubject("Activate status.");
        mailMessage.setFrom("myfirstcalculatorapp@gmail.com");
        mailMessage.setText("To activate status, please click here: "
                + "http://localhost:8080/api/upd/activateStatus?token="
                + token.getToken());
        senderService.sendEmail(mailMessage);
    }

    public ResponseEntity<?> activate(String token) {
        Optional<ActivationToken> byToken = tokenRepo.findByToken(token);
        return byToken.map(activationToken -> userRepo.findByEmail(activationToken.getXUser().getEmail())
                .map(user -> {
                    user.setStatus(true);
                    userRepo.save(user);
                    log.info("Success: User Status activated successfully. User - {}", user.getEmail());
                    return ResponseEntity.ok().body(new MessageResponse(true, 200, "Success: User Status activated successfully."));
                }).orElseGet(() -> ResponseEntity.badRequest().body(new MessageResponse(false, 100, "Error: User not found."))))
                .orElseGet(() -> ResponseEntity.badRequest().body(new MessageResponse(false, 150, "Error: Activation Token couldn't find.")));
    }

    @Override
    public ResponseEntity<?> login(LoginRequest loginRequest, HttpServletRequest request) {
        log.info("User {} is trying to login. User - {} >>> User IP - {}", loginRequest.getEmail(), loginRequest.getEmail(), request.getRemoteAddr());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generateToken(authentication);
        XUserDetails xUserDetails = (XUserDetails) authentication.getPrincipal();
        List<String> roles = xUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        log.info("Success: User successfully login. User - {} >>> User IP - {}", loginRequest.getEmail(), request.getRemoteAddr());
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


    public ResponseEntity<?> update(Map<String, String> updateRequest) {
        Optional<XUser> foundUser = userRepo.findById(Long.valueOf(updateRequest.get("id")));
        if (foundUser.isPresent()) {
            XUser xUser = foundUser.get();
            if (checkNull(updateRequest.get("mobNumber"))) {
                if (userRepo.existsByMobNumber(updateRequest.get("mobNumber"))) {
                    log.warn("Error: New Mobile Number is already in use. User - {}", xUser.getEmail());
                    return ResponseEntity.badRequest().body(new MessageResponse(false, 2, "Error: Mobile Number is already in use."));
                } else xUser.setMobNumber(updateRequest.get("mobNumber"));
            }
            if (checkNull(updateRequest.get("password")) && checkNull(updateRequest.get("passwordConfirm"))) {
                if (!updateRequest.get("password").equals(updateRequest.get("passwordConfirm"))) {
                    log.warn("Error: Confirm Password doesn't match with Password. User - {} ", xUser.getEmail());
                    return ResponseEntity.badRequest().body(new MessageResponse(false, 3, "Error: Confirm Password doesn't match with Password."));

                } else xUser.setPassword(encoder.encode(updateRequest.get("password")));
            }
            if (checkNull(updateRequest.get("name"))) xUser.setName(updateRequest.get("name"));
            if (checkNull(updateRequest.get("language"))) {
                Language language = checkLanguage(updateRequest.get("language"));
                xUser.setLanguage(language);

            }
            userRepo.save(xUser);
            log.info("Success: User successfully updated. User - {} ", xUser.getEmail());
            return ResponseEntity.ok().body(new MessageResponse(true, 200, "Success: User successfully updated."));
        } else return ResponseEntity.badRequest().body(new MessageResponse(false, 10, "Error: User not found."));
    }

    private Language checkLanguage(String language) {
        switch (language) {
            case "AZ":
                return Language.valueOf("AZ");
            case "RU":
                return Language.valueOf("RU");
            default:
                return Language.valueOf("EN");
        }
    }

    public boolean checkNull(String field) {
        return field != null;
    }


}

