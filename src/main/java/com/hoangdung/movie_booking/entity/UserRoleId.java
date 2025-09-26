package com.hoangdung.movie_booking.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
public class UserRoleId implements Serializable {
    private Long user;
    private Long role;
}
