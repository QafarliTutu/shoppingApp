package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubCategory {

    @Id
    @Column(name = "subCategory_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String subCateName;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cate_id",nullable = false)
    private Category category;

    @OneToMany(mappedBy = "subcategory",cascade = CascadeType.ALL)
    private List<Item> item;

}
