package com.example.demo.payload.response;

import com.example.demo.enums.Language;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private String jwtToken;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private List<String> roles;
    private String mobNumber;
    private Language language;

    @Override
    public String toString() {
        return "JwtResponse{" +
                "jwtToken='" + jwtToken + '\'' +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", mobNumber='" + mobNumber + '\'' +
                ", language=" + language +
                '}';
    }
}
