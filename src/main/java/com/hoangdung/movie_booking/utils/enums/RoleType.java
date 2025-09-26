package com.hoangdung.movie_booking.utils.enums;

import lombok.Getter;

@Getter
public enum RoleType {
    USER(1),
    STAFF(2),
    THEATER_MANAGER(3),
    ADMIN(4),
    SUPER_ADMIN(5);

    private final int level;

    RoleType(int level) { this.level = level; }

    public int getLevel() { return level; }
}
