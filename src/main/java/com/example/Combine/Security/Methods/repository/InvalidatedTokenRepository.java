package com.example.Combine.Security.Methods.repository;

import com.example.Combine.Security.Methods.entity.ExpiredToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<ExpiredToken, String> {

    boolean existsByTokenAndExpiryDateAfter(String token, LocalDateTime now);
}
