package com.example.Combine.Security.Methods.dto;

import java.io.Serializable;

public record RegisterDto(
        String username,
        String password
) implements Serializable {
}
