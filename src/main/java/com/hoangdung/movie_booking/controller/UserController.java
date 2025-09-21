package com.hoangdung.movie_booking.controller;

import com.hoangdung.movie_booking.dto.response.BaseResponse;
import com.hoangdung.movie_booking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        log.info("Get info user by userId: {}", id);
        return ResponseEntity.ok(BaseResponse.success("Get info user success",
                userService.getUserById(id)));
    }
}
