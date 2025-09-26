package com.hoangdung.movie_booking.service.impl.User;

import com.hoangdung.movie_booking.dto.request.User.ChangePasswordRequest;
import com.hoangdung.movie_booking.dto.request.User.UserUpdateDetailsRequest;
import com.hoangdung.movie_booking.dto.response.User.RegisterRequest;
import com.hoangdung.movie_booking.dto.response.User.UserDetailsResponse;
import com.hoangdung.movie_booking.dto.response.User.UserInfoResponse;
import com.hoangdung.movie_booking.entity.Role;
import com.hoangdung.movie_booking.entity.User;
import com.hoangdung.movie_booking.exception.ResourceNotFoundException;
import com.hoangdung.movie_booking.helper.User.UserValidateField;
import com.hoangdung.movie_booking.repository.RoleRepository;
import com.hoangdung.movie_booking.repository.UserRepository;
import com.hoangdung.movie_booking.service.User.UserService;
import com.hoangdung.movie_booking.utils.enums.RoleType;
import com.hoangdung.movie_booking.utils.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link UserService} that provides features
 * for managing the currently authenticated user's account.
 *
 * <p>This includes:</p>
 * <ul>
 *   <li>Registering a new account (called by {@code AuthService})</li>
 *   <li>Retrieving basic and detailed profile information</li>
 *   <li>Updating profile details</li>
 *   <li>Changing password</li>
 * </ul>
 *
 * <p>Except for {@link #register(RegisterRequest)}, all methods are
 * intended to be called in the context of an authenticated user.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidateField validateField;

    /**
     * Registers a new user account with default {@link RoleType#USER}.
     *
     * <p>This method is typically called by the authentication service
     * during sign-up. It validates uniqueness of username and email,
     * assigns the default role, and persists the new user.</p>
     *
     * @param request registration details (username, email, password, etc.)
     * @throws ResourceNotFoundException if the default role {@link RoleType#USER} does not exist
     */
    @Transactional
    @Override
    public void register(RegisterRequest request) {
        log.info("Registering new user account");

        validateField.validateUserUniqueFields(request.getUsername(), request.getEmail());

        Role role = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role user not found"));

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)
                .status(UserStatus.ACTIVE)
                .build();
        user.getRoles().add(role);

        userRepository.save(user);
        log.info("Registered new account successfully with username: {}", request.getUsername());
    }

    /**
     * Retrieves basic profile information of the currently logged-in user.
     *
     * <p>Example fields: id, username, name, email.</p>
     *
     * @return {@link UserInfoResponse} containing basic profile information
     * @throws IllegalStateException if no authenticated user is found in context
     */
    @Override
    public UserInfoResponse getInfo() {
        return null; // TODO: implement
    }

    /**
     * Retrieves detailed profile information of the currently logged-in user.
     *
     * <p>Example fields: id, username, name, email, roles, status,
     * date of birth, phone number, avatar URL, etc.</p>
     *
     * @return {@link UserDetailsResponse} containing full profile information
     * @throws IllegalStateException if no authenticated user is found in context
     */
    @Override
    public UserDetailsResponse getDetails() {
        return null; // TODO: implement
    }

    /**
     * Changes the password of the currently logged-in user.
     *
     * <p>Validates the provided old password before updating
     * to the new one. If the validation fails, an exception is thrown.</p>
     *
     * @param request contains oldPassword and newPassword
     * @throws IllegalArgumentException if the old password does not match the current one
     * @throws IllegalStateException if no authenticated user is found in context
     */
    @Override
    public void changePassword(ChangePasswordRequest request) {
        // TODO: implement
    }

    /**
     * Updates profile information of the currently logged-in user.
     *
     * <p>Only non-null fields in {@link UserUpdateDetailsRequest}
     * are updated. For example: name, phone, date of birth, avatar URL.</p>
     *
     * @param request updated profile fields
     * @return {@link UserDetailsResponse} containing the updated profile information
     * @throws IllegalStateException if no authenticated user is found in context
     */
    @Override
    public UserDetailsResponse updateInfo(UserUpdateDetailsRequest request) {
        return null; // TODO: implement
    }
}
