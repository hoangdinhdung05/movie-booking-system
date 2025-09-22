package com.hoangdung.movie_booking.repository;

import com.hoangdung.movie_booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find info user by email
     * @param email email
     * @return Full info user
     */
    Optional<User> findByEmail(String email);

}
