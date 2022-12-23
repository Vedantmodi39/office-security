package com.simform.office.dto;

import com.simform.office.validation.PasswordConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetPassword {
    private long userId;
    private String token;
    @PasswordConstraint
    private String password;
}
