package com.simform.office.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordConstraint {

    String message() default "Password must be 8-20 characters long, must contain one digit, lower case alphabet, upper case alphabet, special character and no white spaces.";

    Class<?>[] groups() default {};

    Class<Payload>[] payload() default {};
}