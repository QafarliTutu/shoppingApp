package com.example.demo.repository;

import com.example.demo.model.XUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<XUser,Long> {

    Optional<XUser> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMobNumber(String number);
}
