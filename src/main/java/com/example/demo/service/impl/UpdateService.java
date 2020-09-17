package com.example.demo.service.impl;

import com.example.demo.enums.Language;
import com.example.demo.model.ActivationToken;
import com.example.demo.model.XUser;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.repository.ActivationTokenRepo;
import com.example.demo.repository.UserRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
public class UpdateService {


    private final ActivationTokenRepo tokenRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder encoder;


    public UpdateService(ActivationTokenRepo tokenRepo, UserRepo userRepo, PasswordEncoder encoder) {
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
        this.encoder = encoder;
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

    public Language checkLanguage(String language) {
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
