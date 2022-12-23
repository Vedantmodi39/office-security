package com.simform.office.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 50)
    private String name;
    @Column(length = 100)
    private String email;
    private String password;
    private String phoneNumber;
    private String resetPasswordToken;
    private LocalDateTime resetPasswordTokenExpiry;
    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted;
    @Column(columnDefinition = "boolean default false")
    private boolean isActive;

    @ManyToOne(cascade = {CascadeType.ALL}, targetEntity = Role.class, fetch = FetchType.EAGER)
    private Role role;
}
