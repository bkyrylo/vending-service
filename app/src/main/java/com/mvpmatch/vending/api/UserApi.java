package com.mvpmatch.vending.api;

import com.mvpmatch.vending.api.dto.CreateUserRequest;
import com.mvpmatch.vending.api.dto.DepositRequest;
import com.mvpmatch.vending.api.dto.UpdateUserRequest;
import com.mvpmatch.vending.api.dto.UserResponse;
import com.mvpmatch.vending.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserApi {

    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        return userService.createUser(CreateUserRequest.asUser(createUserRequest), createUserRequest.getRoleNames())
                .map(UserResponse::from)
                .map(ResponseEntity::ok);
    }

    @PutMapping
    public Mono<ResponseEntity<UserResponse>> updateUser(@AuthenticationPrincipal(expression = "id") UUID userId,
                                                         @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(userId, updateUserRequest.getPassword(), updateUserRequest.getRoleNames())
                          .map(UserResponse::from)
                          .map(ResponseEntity::ok);
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> deleteUser(@AuthenticationPrincipal(expression = "id") UUID userId) {
        return userService.deleteUser(userId)
                .thenReturn(ResponseEntity.accepted().build());
    }

    @GetMapping
    public Mono<ResponseEntity<UserResponse>> getUser(@AuthenticationPrincipal(expression = "id") UUID userId) {
        return userService.findUser(userId)
                          .map(UserResponse::from)
                          .map(ResponseEntity::ok);
    }

    @PostMapping("/reset")
    public Mono<ResponseEntity<UserResponse>> resetDeposit(@AuthenticationPrincipal(expression = "id") UUID userId) {
        return userService.resetUserDeposit(userId)
                          .map(UserResponse::from)
                          .map(ResponseEntity::ok);
    }

    @PostMapping("/deposit")
    public Mono<ResponseEntity<UserResponse>> deposit(@AuthenticationPrincipal(expression = "id") UUID userId,
                                                      @Valid @RequestBody DepositRequest depositRequest) {
        return userService.depositCents(userId, depositRequest.getDepositInCents())
                          .map(UserResponse::from)
                          .map(ResponseEntity::ok);
    }
}
