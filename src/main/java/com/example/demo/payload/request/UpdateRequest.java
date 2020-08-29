package com.example.demo.payload.request;

import com.example.demo.enums.Language;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class UpdateRequest {
    private Long id;

    private String name;

    @Size(min = 8)
    private String password;

    @Size(min = 8)
    private String passwordConfirm;

    @Size(max = 10)
    private String mobNumber;

    private Language language;


}
