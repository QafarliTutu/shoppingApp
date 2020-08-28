package com.example.demo.model;

import com.example.demo.enums.Language;
import com.example.demo.model.Role;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XUser {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private long id;

     private String name;

     private String email;

     private String password;

     private String mobNumber;

     private Language language;

     private boolean status;

     @ManyToMany(fetch = FetchType.LAZY)
     @JoinTable(name = "xuser_roles",
     joinColumns = @JoinColumn(name = "xuser_id"),
     inverseJoinColumns = @JoinColumn(name = "role_id"))
     private Set<Role> roles = new HashSet<>();

    @Override
    public String toString() {
        return "XUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", mobNumber='" + mobNumber + '\'' +
                ", language=" + language +
                ", status=" + status +
                '}';
    }


}