package com.simform.office.controller;

import com.simform.office.dto.AuthenticationRequest;
import com.simform.office.dto.GenericResponse;
import com.simform.office.dto.SetPassword;
import com.simform.office.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    @PostMapping(value = "/login")
    public ResponseEntity<GenericResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        return new ResponseEntity<>(new GenericResponse(true, "user logged in successfully!", userService.login(authenticationRequest), HttpStatus.OK.value(), LocalDateTime.now()), HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<GenericResponse> generateResetPasswordToken(@RequestParam("email") String email) throws MessagingException {
        return new ResponseEntity<>(new GenericResponse(true, "password reset mail sent successfully", userService.generateResetPasswordToken(email), HttpStatus.OK.value(), LocalDateTime.now()), HttpStatus.OK);
    }

    @PostMapping("/setPassword")
    public ResponseEntity<GenericResponse> setPassword(@Valid  @RequestBody SetPassword password) throws MessagingException {
        return new ResponseEntity<>(new GenericResponse(true, "password changed successfully", userService.changePassword(password), HttpStatus.OK.value(), LocalDateTime.now()), HttpStatus.OK);
    }
}
