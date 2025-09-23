package com.hoangdung.movie_booking.repository;

import com.hoangdung.movie_booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


    /**
     * Find user by username
     * @param username username
     * @return User entity
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by username or email
     * @param username request
     * @param  email request
     * @return User
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Find role and permission by username
     * @param username username
     * @return Full info user
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);


}
