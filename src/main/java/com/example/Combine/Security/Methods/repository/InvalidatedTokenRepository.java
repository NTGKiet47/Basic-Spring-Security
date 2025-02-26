package com.example.Combine.Security.Methods.repository;

import com.example.Combine.Security.Methods.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {

    boolean existsByTokenAndExpiryDateAfter(String token, LocalDateTime now);
}
