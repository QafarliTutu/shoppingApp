package com.example.demo.model;

import com.example.demo.enums.Language;
import com.example.demo.model.Role;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
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

     @OneToMany(mappedBy = "xuser",cascade = CascadeType.ALL)
     private List<Item> itemList;

    @Override
    public String toString() {
        return String.format("XUser{id=%d, name='%s', email='%s', password='%s', mobNumber='%s', language=%s, status=%s, roles=%s, itemList=%s}", id, name, email, password, mobNumber, language, status, roles, itemList);
    }
}