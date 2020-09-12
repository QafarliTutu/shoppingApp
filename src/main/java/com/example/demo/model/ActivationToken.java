package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ActivationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private long id;

    private String token;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date creationDate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "xuser_id")
    private XUser xUser;


    public ActivationToken(XUser existingUser) {
        this.xUser = existingUser;
        creationDate = new Date();
        token = UUID.randomUUID().toString();
    }
}
