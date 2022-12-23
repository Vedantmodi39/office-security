package com.simform.office;

import com.simform.office.entity.Role;
import com.simform.office.entity.User;
import com.simform.office.repository.RoleRepository;
import com.simform.office.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SetupLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final String ROLE_ADMIN = "ROLE_ADMIN";
    private final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    private final String ROLE_INTERN = "ROLE_INTERN";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        roleRepository.findByRole(ROLE_ADMIN).orElseGet(() -> {
            Role admin = new Role();
            admin.setRole(ROLE_ADMIN);
            return roleRepository.save(admin);
        });

        roleRepository.findByRole(ROLE_EMPLOYEE).orElseGet(() -> {
            Role employee = new Role();
            employee.setRole(ROLE_EMPLOYEE);
            return roleRepository.save(employee);
        });

        roleRepository.findByRole(ROLE_INTERN).orElseGet(() -> {
            Role employee = new Role();
            employee.setRole(ROLE_INTERN);
            return roleRepository.save(employee);
        });

        userRepository.findByEmail("admin@gmail.com").orElseGet(() -> {
            User adminUser = new User();
            adminUser.setName("Admin");
            adminUser.setEmail("admin@gmail.com");
            //password = 'Admin@123'
            adminUser.setPassword("$2a$12$iKlAjXsZCDWmRUvxNulYGelsA1cXbKLlLxtPG7rfRH38bekf0oE/m");
            adminUser.setPhoneNumber("9106523523");
            adminUser.setActive(true);
            roleRepository.findByRole(ROLE_ADMIN).ifPresent(adminUser::setRole);
            return userRepository.save(adminUser);
        });

        userRepository.findByEmail("employee@gmail.com").orElseGet(() -> {
            User adminUser = new User();
            adminUser.setName("Employee");
            adminUser.setEmail("employee@gmail.com");
            //password = 'Employee@123'
            adminUser.setPassword("$2a$12$7JW0k5Wn4CbpR6GSKx4yN.2EwL4lrgXyKfiRSr4Lt8VuRZQvaakFC");
            adminUser.setPhoneNumber("8106523523");
            adminUser.setActive(true);
            roleRepository.findByRole(ROLE_EMPLOYEE).ifPresent(adminUser::setRole);
            return userRepository.save(adminUser);
        });

        userRepository.findByEmail("intern@gmail.com").orElseGet(() -> {
            User adminUser = new User();
            adminUser.setName("intern");
            adminUser.setEmail("intern@gmail.com");
            //password = 'Intern@123'
            adminUser.setPassword("$2a$12$BqtmrQbQapLe13OnmxYw6.UC1SjPAqSKMkqXj4tJFPiJcQ5mK/lxG");
            adminUser.setPhoneNumber("7106523523");
            adminUser.setActive(true);
            roleRepository.findByRole(ROLE_INTERN).ifPresent(adminUser::setRole);
            return userRepository.save(adminUser);
        });

    }
}
