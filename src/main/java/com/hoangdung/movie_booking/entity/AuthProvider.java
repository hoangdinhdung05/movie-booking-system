package com.hoangdung.movie_booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "auth_providers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_provider", columnNames = {"provider", "provider_id"})
})
public class AuthProvider extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String provider; // GOOGLE, FACEBOOK, APPLE...

    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;
}
