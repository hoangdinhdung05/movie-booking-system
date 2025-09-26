package com.hoangdung.movie_booking.dto.request.User;

import com.hoangdung.movie_booking.utils.enums.RoleType;
import com.hoangdung.movie_booking.utils.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import java.util.Set;

@Getter
public class AdminUpdateRequest {
    private long id;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "phone_verified")
    private boolean phoneVerified;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserStatus status;

    private Set<RoleType> roles;
}
