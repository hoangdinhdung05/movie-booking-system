package com.hoangdung.movie_booking.repository;

import com.hoangdung.movie_booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     * @param username username
     * @return User entity
     */
    Optional<User> findByUsername(String username);

}
