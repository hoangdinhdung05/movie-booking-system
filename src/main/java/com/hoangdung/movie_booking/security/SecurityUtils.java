package com.hoangdung.movie_booking.security;

import com.hoangdung.movie_booking.entity.User;
import com.hoangdung.movie_booking.utils.enums.RoleType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static User getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return (User) authentication.getPrincipal();
        }
        throw new BadCredentialsException("No authenticated user found");
    }

    public static Long getCurrentUserId() {
        return getCurrentUserDetails().getId();
    }

    public static boolean hasRole(RoleType type) {
        return getCurrentUserDetails().getRoles().stream()
                .anyMatch(role -> role.getName().equals(type.name()));
    }

}
