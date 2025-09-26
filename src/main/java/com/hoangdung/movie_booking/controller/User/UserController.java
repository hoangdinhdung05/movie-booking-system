package com.hoangdung.movie_booking.controller.User;

import com.hoangdung.movie_booking.dto.request.User.ChangePasswordRequest;
import com.hoangdung.movie_booking.dto.request.User.UserUpdateDetailsRequest;
import com.hoangdung.movie_booking.dto.response.User.UserDetailsResponse;
import com.hoangdung.movie_booking.dto.response.User.UserInfoResponse;
import com.hoangdung.movie_booking.service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling operations related to the currently authenticated user.
 *
 * <p>This controller provides endpoints for managing the
 * account of the logged-in user, including:</p>
 * <ul>
 *   <li>Retrieving basic and detailed profile information</li>
 *   <li>Updating personal profile details</li>
 *   <li>Changing account password</li>
 * </ul>
 *
 * <p>All endpoints in this controller operate on the context
 * of the currently authenticated user. Authentication is required.</p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retrieves basic profile information of the currently authenticated user.
     *
     * <p>This typically includes general account info such as
     * username, email, and role.</p>
     *
     * @return a {@link UserInfoResponse} containing basic user information
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getInfo() {
        return ResponseEntity.ok(userService.getInfo());
    }

    /**
     * Retrieves full detailed information of the currently authenticated user.
     *
     * <p>Compared to {@link #getInfo()}, this provides
     * more complete data, such as personal details and additional attributes.</p>
     *
     * @return a {@link UserDetailsResponse} containing full user profile details
     */
    @GetMapping("/me/details")
    public ResponseEntity<UserDetailsResponse> getDetails() {
        return ResponseEntity.ok(userService.getDetails());
    }

    /**
     * Updates the password of the currently authenticated user.
     *
     * <p>The request must include the current password and a valid new password.</p>
     *
     * @param request the {@link ChangePasswordRequest} containing oldPassword and newPassword
     * @return HTTP 200 OK if the password was successfully updated
     */
    @PutMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates the profile information of the currently authenticated user.
     *
     * <p>This may include fields such as name, phone, date of birth, or avatar URL.</p>
     *
     * @param request the {@link UserUpdateDetailsRequest} containing updated profile fields
     * @return the updated {@link UserDetailsResponse} with new user profile information
     */
    @PutMapping("/me/update")
    public ResponseEntity<UserDetailsResponse> updateInfo(@RequestBody UserUpdateDetailsRequest request) {
        return ResponseEntity.ok(userService.updateInfo(request));
    }
}
