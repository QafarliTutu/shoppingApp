package com.example.demo.payload.request;

import lombok.*;

import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Validated
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdvertiseRequest {

    @NotBlank
    private String city;

    @NotNull
    private double price;

    @NotBlank
    private String content;

    // @NotBlank
    private String pictureLink;

    private String category;

    private String subcategory;


}
