package com.hoangdung.movie_booking.helper.User;

import com.hoangdung.movie_booking.entity.User;
import com.hoangdung.movie_booking.exception.UserAlreadyExistsException;
import com.hoangdung.movie_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserValidateField {

    private final UserRepository userRepository;

    public void validateUserUniqueFields(String username, String email) {
        Optional<User> existingUser = userRepository.findByUsernameOrEmail(username, email);
        if (existingUser.isPresent()) {
            if (existingUser.get().getUsername().equals(username)) {
                throw new UserAlreadyExistsException("User already exists: " + username);
            }
            if (existingUser.get().getEmail().equals(email.toLowerCase())) {
                throw new UserAlreadyExistsException("Email already exists: " + email);
            }
        }

    }

}
