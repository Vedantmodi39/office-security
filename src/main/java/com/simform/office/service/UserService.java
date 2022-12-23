package com.simform.office.service;

import com.simform.office.dto.*;
import com.simform.office.entity.User;
import com.simform.office.exception.ResourceNotFoundException;
import com.simform.office.exception.UserAlreadyExistsException;
import com.simform.office.repository.RoleRepository;
import com.simform.office.repository.UserRepository;
import com.simform.office.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MailService mailService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(AuthenticationRequest request) {
        org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword(), userDetails.getAuthorities());
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with email id '" + userDetails.getUsername() + "' not found"));
        return new JwtResponse(JwtUtil.generateToken(user.getEmail()), LocalDateTime.now().plusDays(1));
    }

    public UserDetails addEmployee(UserDetails userDetails) throws MessagingException {
        if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email id '" + userDetails.getEmail() + "' already exists");
        }

        String resetPasswordToken = UUID.randomUUID().toString();

        User user = new User();
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        roleRepository.findByRole(userDetails.getRole()).ifPresent(user::setRole);
        user.setResetPasswordToken(resetPasswordToken);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusDays(1));
        user = userRepository.save(user);
        mailService.sendResetPasswordMail(user.getEmail(), user.getName(), resetPasswordToken, user.getId());
        return userDetails;
    }

    public ResetPasswordToken generateResetPasswordToken(String email) throws MessagingException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email id '" + email + "' not found!"));
        String resetPasswordToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetPasswordToken);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusDays(1));
        user = userRepository.save(user);
        mailService.sendResetPasswordMail(email, user.getName(), resetPasswordToken, user.getId());
        return new ResetPasswordToken(user.getId(), resetPasswordToken, user.getResetPasswordTokenExpiry());
    }

    public String changePassword(SetPassword password) {
        User user = userRepository.findById(password.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User with id'" + password.getUserId() + "' not found"));
        if (user.getResetPasswordToken() == null || user.getResetPasswordTokenExpiry() == null || user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Token has expired!");
        }
        user.setPassword(passwordEncoder.encode(password.getPassword()));
        user.setResetPasswordTokenExpiry(null);
        user.setResetPasswordToken(null);
        user.setActive(true);
        userRepository.save(user);
        return "Success";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostFilter("filterObject.email != authentication.principal.username")
    public List<UserDetails> getUserDetails() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isDeleted())
                .map(user -> new UserDetails(
                        user.getName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRole().getRole())
                ).collect(Collectors.toList());
    }

    public List<UserDetails> getEmployees() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isDeleted())
                .filter(user -> Objects.equals(user.getRole().getRole(), "ROLE_EMPLOYEE"))
                .map(user -> new UserDetails(
                        user.getName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRole().getRole())
                ).toList();
    }

    public UserDetails findEmployeeById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id'" + id + "' not found"));
        return new UserDetails(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getRole().getRole());
    }

    public UserDetails deleteEmployee(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id'" + id + "' not found"));
        user.setDeleted(true);
        user.setActive(false);
        user = userRepository.save(user);
        return new UserDetails(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getRole().getRole());
    }

    public UserDetails findMyRole(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email id'" + email + "' not found"));
        return new UserDetails(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getRole().getRole());
    }
}

