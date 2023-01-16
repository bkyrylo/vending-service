package com.mvpmatch.vending.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mvpmatch.vending.api.validation.AllowedRoles;
import com.mvpmatch.vending.entity.Role;
import com.mvpmatch.vending.entity.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class CreateUserRequest {

    @NotNull
    @Size(max = 64, message = "User name should not exceeded 64 characters")
    String username;

    @NotNull
    @Size(max = 64, message = "Password should not exceeded 64 characters")
    String password;

    @JsonProperty("roles")
    @NotEmpty
    @Size(max = 2) List<@AllowedRoles String> roleNames;

    public static User asUser(CreateUserRequest createUserRequest) {
        return User.builder()
                .username(createUserRequest.getUsername())
                .password(createUserRequest.getPassword())
                .build();
    }
}