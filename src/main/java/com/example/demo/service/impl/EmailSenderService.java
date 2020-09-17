package com.example.demo.service.impl;

import com.example.demo.model.ActivationToken;
import com.example.demo.model.XUser;
import com.example.demo.repository.ActivationTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class EmailSenderService {


    private final JavaMailSender javaMailSender;
    private final ActivationTokenRepo tokenRepo;

    @Autowired
    public EmailSenderService(JavaMailSender javaMailSender, ActivationTokenRepo tokenRepo) {
        this.javaMailSender = javaMailSender;
        this.tokenRepo = tokenRepo;
    }

    public void sendEmail(XUser xUser) {
        ActivationToken token = new ActivationToken(xUser);
        tokenRepo.save(token);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(xUser.getEmail());
        mailMessage.setSubject("Activate status.");
        mailMessage.setFrom("myfirstcalculatorapp@gmail.com");
        mailMessage.setText("To activate status, please click here: "
                + "http://localhost:8080/api/upd/activateStatus?token="
                + token.getToken());
        send(mailMessage);
    }

    @Async
    public void send(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

}