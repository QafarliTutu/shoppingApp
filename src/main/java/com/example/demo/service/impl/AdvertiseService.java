package com.example.demo.service.impl;

import com.example.demo.config.JwtTokenService;
import com.example.demo.model.Category;
import com.example.demo.model.Item;
import com.example.demo.model.SubCategory;
import com.example.demo.model.XUser;
import com.example.demo.payload.request.AdvertiseRequest;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.repository.CategoryRepo;
import com.example.demo.repository.ItemRepo;
import com.example.demo.repository.SubCategoryRepo;
import com.example.demo.repository.UserRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@Log4j2
public class AdvertiseService {
    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private SubCategoryRepo subCategoryRepo;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepo userRepo;

    public ResponseEntity<?> postAdv(AdvertiseRequest advRequest, String jwt) {
        if (jwt == null || !jwtTokenService.validateToken(jwt)) return ResponseEntity.badRequest().body(new MessageResponse(false,520,"Token not found."));
        String xUserEmail = jwtTokenService.getUsernameFromToken(jwt);
        Optional<XUser> byEmail = userRepo.findByEmail(xUserEmail);
        if (byEmail.isPresent()) {
            Item item = buildAdv(advRequest, byEmail.get());
            itemRepo.save(item);
            log.info("Success: User posted advertise successfully. User - {}", byEmail.get().getEmail());
            return ResponseEntity.ok().body(new MessageResponse(true,0,"Success: User posted advertise successfully."));
        }else {
            log.warn("Error: User couldn't found. User - {}",xUserEmail);
            return ResponseEntity.ok().body(new MessageResponse(true,0,"Error: User couldn't found."));
        }
    }

    public Item buildAdv(AdvertiseRequest advertiseRequest, XUser user) {
        return Item.builder()
                .city(advertiseRequest.getCity())
                .content(advertiseRequest.getContent())
                .price(advertiseRequest.getPrice())
                .categoryinItem(categoryRepo.findByCateName(advertiseRequest.getCategory()).orElseThrow(() -> new RuntimeException("Category not found.")))
                .subcategory(subCategoryRepo.findBySubCateName(advertiseRequest.getSubcategory()).orElseThrow(()-> new RuntimeException("SubCategory not found.")))
                .pictureLink(advertiseRequest.getPictureLink())
                .xuser(user)
                .build();
        }

}

