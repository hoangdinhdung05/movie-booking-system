package com.hoangdung.movie_booking.repository;

import com.hoangdung.movie_booking.entity.Role;
import com.hoangdung.movie_booking.utils.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     * @param name name
     * @return Role
     */
    Optional<Role> findByName(RoleType name);

    /**
     * Find role by name
     * @return List role
     */
    List<Role> findByNameIn(Collection<RoleType> names);

}
