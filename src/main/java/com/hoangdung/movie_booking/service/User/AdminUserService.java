package com.hoangdung.movie_booking.service.User;

import com.hoangdung.movie_booking.dto.request.User.AdminUpdateRequest;
import com.hoangdung.movie_booking.dto.response.System.PageResponse;
import com.hoangdung.movie_booking.dto.response.User.AdminCreateUserRequest;
import com.hoangdung.movie_booking.dto.response.User.UserResponse;
import com.hoangdung.movie_booking.entity.User;

/**
 * Service interface for Admin to manage users in the movie booking system.
 * Provides operations for creating, updating, retrieving, and deleting users,
 * as well as pagination support for listing users.
 */
public interface AdminUserService {

    /**
     * Finds a user by email.
     * This method should be used internally by Admin to check if a user exists
     * or to retrieve user information by email address.
     *
     * @param email the email of the user
     * @return the {@link User} entity if found
     */
    User getUserByEmail(String email);

    /**
     * Retrieves a user's information by their unique ID.
     * This method maps the entity to a DTO for returning user details
     * to the client without exposing the entity directly.
     *
     * @param id the unique identifier of the user
     * @return a {@link UserResponse} containing the user's information
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves the full {@link User} entity by ID.
     * Unlike {@code getUserById}, this method returns the raw entity
     * and is intended for internal service-layer use.
     *
     * @param id the unique identifier of the user
     * @return the {@link User} entity
     */
    User getUserDetails(Long id);

    /**
     * Creates a new user account by an Admin.
     * Admin can set roles, status, and additional information
     * when creating the account.
     *
     * @param request the information for creating a new user
     * @return UserResponse
     */
    UserResponse adminCreateUser(AdminCreateUserRequest request);

    /**
     * Updates an existing user's information based on Admin's request.
     * Admin may update roles, account status, or verification flags.
     *
     * @param request the update information containing user ID and new values
     * @return a {@link UserResponse} containing the updated user information
     */
    UserResponse update(AdminUpdateRequest request);

    /**
     * Deletes (or deactivates) a user by their ID.
     * Depending on implementation, this may be a soft delete (set status to INACTIVE)
     * or a hard delete (remove the user from the database).
     *
     * @param id the unique identifier of the user
     */
    void delete(Long id);

    /**
     * Retrieves a paginated list of all users in the system.
     *
     * @param pageNumber the page index, starting from 0
     * @param pageSize   the number of users per page
     * @return a {@link PageResponse} containing user data with pagination details
     */
    PageResponse<?> getAll(int pageNumber, int pageSize);
}
