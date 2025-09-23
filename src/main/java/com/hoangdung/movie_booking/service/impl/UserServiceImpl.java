package com.hoangdung.movie_booking.service.impl;

import com.hoangdung.movie_booking.dto.response.User.AdminCreateUserRequest;
import com.hoangdung.movie_booking.dto.response.User.RegisterRequest;
import com.hoangdung.movie_booking.entity.Role;
import com.hoangdung.movie_booking.entity.User;
import com.hoangdung.movie_booking.exception.ResourceNotFoundException;
import com.hoangdung.movie_booking.helper.Role.RoleValidate;
import com.hoangdung.movie_booking.helper.User.UserValidateField;
import com.hoangdung.movie_booking.repository.RoleRepository;
import com.hoangdung.movie_booking.repository.UserRepository;
import com.hoangdung.movie_booking.dto.response.User.UserResponse;
import com.hoangdung.movie_booking.service.UserService;
import com.hoangdung.movie_booking.utils.enums.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidateField validateField;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Find user by email
     *
     * @param email userId
     * @return Get info user
     */
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Find user by userId
     *
     * @param id userId
     * @return Get info user
     */
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus())
                .build();
    }

    @Override
    public void createUser(RegisterRequest request) {
        log.info("Create a user by registering an account");

        validateField.validateUserUniqueFields(request.getUsername(), request.getEmail());
        Role role = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role user not found"));

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)
                .build();
        user.getRoles().add(role);

        userRepository.save(user);
        log.info("Register new account successfully with username: {}", request.getUsername());
    }

    @Override
    public void adminCreateUser(AdminCreateUserRequest request) {
        log.info("Admin create new user");

        validateField.validateUserUniqueFields(request.getUsername(), request.getEmail());

        RoleValidate roleValidator = new RoleValidate(roleRepository);
        Set<Role> roles = roleValidator.validateAndGetRoles(request.getRoles());

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)
                .build();
        user.setRoles(roles);

        userRepository.save(user);
        log.info("Admin create new account successfully with username: {}", request.getUsername());
    }
}
