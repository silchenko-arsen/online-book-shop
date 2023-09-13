package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "role_name", unique = true, nullable = false)
    private RoleName role;
    @Column(nullable = false)
    private boolean isDeleted;
    public enum RoleName {
        ADMIN, USER
    }
}
