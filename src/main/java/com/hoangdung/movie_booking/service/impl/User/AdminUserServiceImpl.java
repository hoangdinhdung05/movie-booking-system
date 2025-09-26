package com.hoangdung.movie_booking.service.impl.User;

import com.hoangdung.movie_booking.dto.request.User.AdminUpdateRequest;
import com.hoangdung.movie_booking.dto.response.System.PageResponse;
import com.hoangdung.movie_booking.dto.response.User.AdminCreateUserRequest;
import com.hoangdung.movie_booking.entity.Role;
import com.hoangdung.movie_booking.entity.User;
import com.hoangdung.movie_booking.exception.ResourceNotFoundException;
import com.hoangdung.movie_booking.helper.Role.RoleValidate;
import com.hoangdung.movie_booking.helper.User.UserValidateField;
import com.hoangdung.movie_booking.mapper.UserMapper;
import com.hoangdung.movie_booking.repository.UserRepository;
import com.hoangdung.movie_booking.dto.response.User.UserResponse;
import com.hoangdung.movie_booking.service.User.AdminUserService;
import com.hoangdung.movie_booking.utils.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

/**
 * Implementation of {@link AdminUserService} providing user management
 * capabilities for administrators.
 *
 * <p>This service allows admins to perform CRUD operations on users,
 * including creation, updating, soft deletion, and retrieval with pagination.
 * It also validates unique fields and ensures that user roles
 * comply with system requirements.</p>
 *
 * <p>Dependencies injected:</p>
 * <ul>
 *   <li>{@link UserRepository} - data access for {@link User} entities</li>
 *   <li>{@link PasswordEncoder} - to securely encode user passwords</li>
 *   <li>{@link RoleValidate} - to validate and fetch system roles</li>
 *   <li>{@link UserValidateField} - to ensure unique fields (email, username)</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleValidate roleValidate;
    private final UserValidateField validateField;

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user
     * @return the {@link User} entity
     * @throws ResourceNotFoundException if no user with the given email is found
     */
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Retrieves a user's details by their ID.
     * This method converts the entity into a {@link UserResponse}.
     *
     * @param id the unique identifier of the user
     * @return a {@link UserResponse} with user information
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.convertToUserResponse(user);
    }

    /**
     * Retrieves the full {@link User} entity by ID.
     * Unlike {@link #getUserById(Long)}, this method returns
     * the raw entity, suitable for internal service-layer use.
     *
     * @param id the unique identifier of the user
     * @return the {@link User} entity
     * @throws ResourceNotFoundException if no user is found with the given ID
     */
    @Override
    public User getUserDetails(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Creates a new user as an administrator.
     * <p>Steps include:</p>
     * <ul>
     *   <li>Validating unique username and email</li>
     *   <li>Validating and assigning roles</li>
     *   <li>Encoding the user's password</li>
     *   <li>Setting default status and verification flags</li>
     * </ul>
     *
     * @param request the request object containing user creation details
     * @return UserResponse
     */
    @Override
    public UserResponse adminCreateUser(AdminCreateUserRequest request) {
        log.info("Admin create new user");

        validateField.validateUserUniqueFields(request.getUsername(), request.getEmail());

        Set<Role> roles = roleValidate.validateAndGetRoles(request.getRoles());

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(true)
                .status(UserStatus.ACTIVE)
                .build();
        user.setRoles(roles);

        userRepository.save(user);
        log.info("Admin create new account successfully with username: {}", request.getUsername());

        return UserMapper.convertToUserResponse(user);
    }

    /**
     * Updates an existing user's information based on an admin's request.
     * Admin may update:
     * <ul>
     *   <li>User roles</li>
     *   <li>Email verification status</li>
     *   <li>Phone verification status</li>
     *   <li>User account status</li>
     * </ul>
     *
     * @param request the update request containing user ID and new values
     * @return a {@link UserResponse} containing the updated user info
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Override
    public UserResponse update(AdminUpdateRequest request) {
        log.info("Admin update user");

        User user = getUserDetails(request.getId());

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = roleValidate.validateAndGetRoles(request.getRoles());
            user.setRoles(roles);
        }

        user.setEmailVerified(request.isEmailVerified());
        user.setPhoneVerified(request.isPhoneVerified());

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .status(user.getStatus())
                .build();
    }

    /**
     * Performs a soft delete of a user account by setting their status to {@link UserStatus#INACTIVE}.
     *
     * @param id the unique identifier of the user
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Override
    public void delete(Long id) {
        log.info("Admin delete user");
        User user = getUserDetails(id);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    /**
     * Retrieves a paginated list of all users in the system.
     *
     * @param pageNumber the page index (0-based)
     * @param pageSize   the number of users per page
     * @return a {@link PageResponse} containing paginated user data
     */
    @Override
    public PageResponse<UserResponse> getAll(int pageNumber, int pageSize) {
        log.info("Admin get list user");

        Page<UserResponse> userPage = userRepository.findAll(PageRequest.of(pageNumber, pageSize))
                .map(UserMapper::convertToUserResponse);
        return PageResponse.of(userPage);
    }
}
