package com.simform.office.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordToken {
    private long userId;
    private String token;
    private LocalDateTime expiry;
}
