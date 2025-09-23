package com.hoangdung.movie_booking.helper.Role;

import com.hoangdung.movie_booking.entity.Role;
import com.hoangdung.movie_booking.exception.ResourceNotFoundException;
import com.hoangdung.movie_booking.repository.RoleRepository;
import com.hoangdung.movie_booking.utils.enums.RoleType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record RoleValidate(RoleRepository roleRepository) {

    public Set<Role> validateAndGetRoles(Set<RoleType> requestRole) {
        //Null -> default role(User)
        Set<RoleType> roleTypes = (requestRole == null || requestRole.isEmpty())
                ? Set.of(RoleType.USER)
                : requestRole.stream().distinct().collect(Collectors.toSet());
        List<Role> roles = roleRepository.findByNameIn(roleTypes);

        if (roles.size() != roleTypes.size()) {
            throw new ResourceNotFoundException("Some roles not found: " + roleTypes);
        }

        return Set.copyOf(roles);
    }

}
