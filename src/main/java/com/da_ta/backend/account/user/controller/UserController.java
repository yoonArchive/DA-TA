package com.da_ta.backend.account.user.controller;

import com.da_ta.backend.account.jwt.JwtTokenProvider;
import com.da_ta.backend.account.user.controller.dto.*;
import com.da_ta.backend.account.user.service.UserService;
import com.da_ta.backend.common.domain.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        HttpHeaders headers = new HttpHeaders();
        LoginResponse loginResponse = userService.login(loginRequest, headers);
        log.info("header: " + headers);
        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(loginResponse);
    }

    @GetMapping("/reissue")
    public ResponseEntity<Message> reissue(@RequestHeader(AUTHORIZATION) String token) {
        HttpHeaders headers = new HttpHeaders();
        Message message = userService.reissue(headers, token);
        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(message);
    }

    @GetMapping
    public ResponseEntity<MyPageResponse> getMyPage(@RequestHeader(AUTHORIZATION) String token) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findSetting(jwtTokenProvider.findUserByToken(token)));
    }

    @PutMapping("/update/1")
    public ResponseEntity<Message> updateAgeRange(@RequestHeader(AUTHORIZATION) String token,
                                                  @RequestBody UpdateAgeRangeRequest updateAgeRangeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.updateAgeRange(jwtTokenProvider.findUserByToken(token), updateAgeRangeRequest));
    }

    @PutMapping("/update/2")
    public ResponseEntity<Message> updateAlertOption(@RequestHeader(AUTHORIZATION) String token,
                                                     @RequestBody UpdateAlertOptionRequest updateAlertOptionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.updateAlertOption(jwtTokenProvider.findUserByToken(token), updateAlertOptionRequest));
    }

    @DeleteMapping
    public ResponseEntity<Message> deleteUser(@RequestHeader(AUTHORIZATION) String token) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.deleteUser(jwtTokenProvider.findUserByToken(token)));
    }
}