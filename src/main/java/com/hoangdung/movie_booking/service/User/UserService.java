package com.hoangdung.movie_booking.service.User;

import com.hoangdung.movie_booking.dto.request.User.ChangePasswordRequest;
import com.hoangdung.movie_booking.dto.request.User.UserUpdateDetailsRequest;
import com.hoangdung.movie_booking.dto.response.User.RegisterRequest;
import com.hoangdung.movie_booking.dto.response.User.UserDetailsResponse;
import com.hoangdung.movie_booking.dto.response.User.UserInfoResponse;
import com.hoangdung.movie_booking.entity.User;

/**
 * Service interface for handling user-related operations
 * that are performed by the currently authenticated user.
 *
 * <p>These methods require the caller to be authenticated
 * (validated via token/session) and operate on the logged-in user's account.</p>
 */
public interface UserService {

    /**
     * Register a new user account.
     *
     * @param request registration details (e.g., email, password, name, etc.)
     */
    void register(RegisterRequest request);

    /**
     * Retrieve basic profile information of the currently logged-in user.
     *
     * @return {@link UserInfoResponse} containing user's profile info
     */
    UserInfoResponse getInfo();

    /**
     * Retrieve detailed profile information of the currently logged-in user.
     *
     * @return {@link UserDetailsResponse} containing full details of the authenticated user
     */
    UserDetailsResponse getDetails();

    /**
     * Change password of the currently logged-in user.
     *
     * @param request contains old password and new password
     */
    void changePassword(ChangePasswordRequest request);

    /**
     * Update profile information of the currently logged-in user.
     *
     * @param request user entity with updated fields
     * @return updated {@link User} entity
     */
    UserDetailsResponse updateInfo(UserUpdateDetailsRequest request);
}
