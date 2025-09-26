package com.hoangdung.movie_booking.controller.User;

import com.hoangdung.movie_booking.dto.request.User.AdminUpdateRequest;
import com.hoangdung.movie_booking.dto.response.System.BaseResponse;
import com.hoangdung.movie_booking.dto.response.System.PageResponse;
import com.hoangdung.movie_booking.dto.response.User.AdminCreateUserRequest;
import com.hoangdung.movie_booking.dto.response.User.UserResponse;
import com.hoangdung.movie_booking.service.User.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for administrator user management.
 *
 * <p>This controller provides endpoints for performing
 * CRUD operations on user accounts, including:
 * <ul>
 *   <li>Creating new users</li>
 *   <li>Updating existing users</li>
 *   <li>Soft deleting users</li>
 *   <li>Retrieving users by ID or email</li>
 *   <li>Listing all users with pagination</li>
 * </ul>
 *
 * <p>All operations are intended to be performed
 * by system administrators.</p>
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * Creates a new user account as an administrator.
     *
     * @param request the request body containing user details
     * @return HTTP 201 Created if successful
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody AdminCreateUserRequest request) {
        log.info("API request: create new user");
        return ResponseEntity.ok(BaseResponse.success("Admin create user success", adminUserService.adminCreateUser(request)));
    }

    /**
     * Updates an existing user's information.
     *
     * @param request the request body containing updated user details
     * @return the updated {@link UserResponse}
     */
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody AdminUpdateRequest request) {
        log.info("API request: update user with id={}", request.getId());
        UserResponse response = adminUserService.update(request);
        return ResponseEntity.ok(BaseResponse.success("Update user success", request));
    }

    /**
     * Performs a soft delete of a user account.
     * The user's status is set to {@code INACTIVE}.
     *
     * @param id the unique identifier of the user
     * @return HTTP 204 No Content if deletion was successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("API request: delete user with id={}", id);
        adminUserService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves user details by ID.
     *
     * @param id the unique identifier of the user
     * @return a {@link UserResponse} containing the user's information
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        log.info("API request: get user by id={}", id);
        UserResponse response = adminUserService.getUserById(id);
        return ResponseEntity.ok(BaseResponse.success("Get user by id success", response));
    }

    /**
     * Retrieves a paginated list of all users in the system.
     *
     * @param page the page index (0-based)
     * @param size the number of users per page
     * @return a {@link PageResponse} containing user data
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("API request: get all users, page={}, size={}", page, size);
        PageResponse<?> response = adminUserService.getAll(page, size);
        return ResponseEntity.ok(BaseResponse.success("Get all users successfully", response));
    }
}
