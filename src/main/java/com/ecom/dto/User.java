package com.ecom.dto;

import com.ecom.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@NoArgsConstructor
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private UserRole role = UserRole.CUSTOMER;
    private Address address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
