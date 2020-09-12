package com.example.demo.repository;

import com.example.demo.model.Item;
import com.example.demo.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface SubCategoryRepo extends JpaRepository<SubCategory,Long> {

    Optional<SubCategory> findBySubCateName(String name);
}
