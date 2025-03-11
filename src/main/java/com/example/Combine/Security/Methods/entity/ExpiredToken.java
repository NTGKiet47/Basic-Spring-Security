package com.example.Combine.Security.Methods.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "invalid_token")
public class ExpiredToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String token;

    private LocalDateTime expiryDate;

}
