package com.example.demo.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;


@Entity
@Validated
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String city;

    private double price;

    private String content;

    private String pictureLink;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cate_id", nullable = false)
    private Category categoryinItem;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subcate_id", nullable = false)
    private SubCategory subcategory;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "xuser_id",nullable = false)
    private XUser xuser;

}
